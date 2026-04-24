package pl.pjatk.alertwip.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class ZabbixSyncService {

    private final ZabbixApiService zabbixApiService;
    private final GlobalProblemRepository problemRepository;
    private final SseNotifService sseService;
    private final SystemSettingService settingService;

    // Klucz dla naszego wewnętrznego alertu o awarii komunikacji z Zabbixem
    private static final String API_ERROR_UNIQUE_KEY = "[SYSTEM] Zabbix API Offline";

    public ZabbixSyncService(ZabbixApiService zabbixApiService,
                             GlobalProblemRepository problemRepository,
                             SseNotifService sseService,
                             SystemSettingService settingService) {
        this.zabbixApiService = zabbixApiService;
        this.problemRepository = problemRepository;
        this.sseService = sseService;
        this.settingService = settingService;
    }

    @Scheduled(fixedRate = 60000) // Uruchamia się co 60 sekund
    @Transactional
    public void syncAlerts() {
        if (!settingService.getBoolean("zabbix_enabled", false)) {
            // Zabbix jest wyłączony, wychodzimy
            return;
        }

        try {
            // Próba pobrania danych z API Zabbixa
            List<Map<String, Object>> currentProblems = zabbixApiService.getActiveProblems();

            if (currentProblems == null) {
                // Jeśli zwraca null, traktujemy to jako błąd komunikacji i zrzucamy do catcha
                throw new RuntimeException("Zabbix API zwróciło pustą odpowiedź (null).");
            }

            // Połączenie działa - zamykamy ewentualny alert systemowy
            resolveSystemAlert(API_ERROR_UNIQUE_KEY);

            System.out.println("[ZABBIX SYNC] Synchronizacja. Aktywne problemy w Zabbix: " + currentProblems.size());

            // 1. Zbieramy unikalne klucze trwających problemów do późniejszego porównania
            Set<String> activeZabbixProblemKeys = new HashSet<>();

            for (Map<String, Object> problemData : currentProblems) {
                Object nameObj = problemData.get("name");
                String name = (nameObj != null) ? nameObj.toString() : "Nieznany błąd";

                int severity = 0;
                Object severityObj = problemData.get("severity");
                if (severityObj != null) {
                    try {
                        severity = Integer.parseInt(severityObj.toString());
                    } catch (NumberFormatException ignored) {}
                }

                // Bezpieczne wyciąganie nazwy hosta z zagnieżdżonej tablicy "hosts"
                String hostName = "Nieznany host";
                Object hostsObj = problemData.get("hosts");

                if (hostsObj instanceof List<?> hostList && !hostList.isEmpty()) {
                    Object firstHostObj = hostList.get(0);
                    if (firstHostObj instanceof Map<?, ?> hostMap) {
                        Object hostValue = hostMap.get("host");
                        if (hostValue != null) {
                            hostName = hostValue.toString();
                        }
                    }
                }

                // Budujemy unikalny klucz identyczny z tym z Webhooka (łączymy host i problem)
                String uniqueProblemKey = "[ZABBIX] " + hostName + " - " + name;
                activeZabbixProblemKeys.add(uniqueProblemKey);

                // 2. SZUKANIE ZGUBIONYCH ALERTÓW (Są w Zabbixie, a nie ma ich u nas JAKO OTWARTE)
                Optional<GlobalProblem> existingProblem = problemRepository.findByUniqueKey(uniqueProblemKey)
                        .filter(p -> !"Done".equals(p.getStatus())); // Szukamy tylko wśród NIEZAMKNIĘTYCH

                if (existingProblem.isEmpty()) {
                    GlobalProblem newProblem = new GlobalProblem();
                    newProblem.setUniqueKey(uniqueProblemKey);
                    newProblem.setSubject(name);
                    newProblem.setSource(hostName);
                    newProblem.setContent("Problem dodany przez Sync API.");
                    newProblem.setStatus("Sent"); // Nowy alert
                    newProblem.setSeverity(severity);
                    newProblem.setCreatedAt(LocalDateTime.now());

                    GlobalProblem saved = problemRepository.save(newProblem);

                    sseService.sendAlert("NEW_ALERT", saved);
                    System.out.println("[ZABBIX SYNC] Dodano zgubiony alert: " + uniqueProblemKey + " (Severity: " + severity + ")");
                }
            }

            // 3. SZUKANIE ZGUBIONYCH ROZWIĄZAŃ (Są otwarte u nas, ale zniknęły już z Zabbixa)
            List<GlobalProblem> dbZabbixProblems = problemRepository.findAll().stream()
                    .filter(p -> p.getUniqueKey() != null && p.getUniqueKey().startsWith("[ZABBIX]"))
                    .filter(p -> !"Done".equals(p.getStatus())) // Patrzymy tylko na otwarte
                    .toList();

            for (GlobalProblem dbProblem : dbZabbixProblems) {
                // Jeśli problem z bazy NIE ZNAJDUJE SIĘ już na liście pobranej z Zabbixa...
                if (!activeZabbixProblemKeys.contains(dbProblem.getUniqueKey())) {

                    // Zamiast usuwać, ZAMYKAMY ALERT (Soft Delete)
                    dbProblem.setStatus("Done");
                    dbProblem.setClosedAt(LocalDateTime.now());
                    problemRepository.save(dbProblem);

                    // Informujemy Vue, że alert został rozwiązany
                    sseService.sendAlert("ALERT_RESOLVED", dbProblem);
                    System.out.println("[ZABBIX SYNC] Wykryto rozwiązanie. Alert zamknięty: " + dbProblem.getUniqueKey());
                }
            }

        } catch (Exception e) {
            // --- AWARIA POŁĄCZENIA ---
            System.err.println("[ZABBIX SYNC] Awaria połączenia. Generuję alert systemowy dla Vue...");
            triggerSystemAlert(API_ERROR_UNIQUE_KEY, "Brak komunikacji z Zabbix API: " + e.getMessage());
        }
    }


    // ==========================================
    // METODY POMOCNICZE DLA ALERTÓW SYSTEMOWYCH
    // ==========================================

    private void triggerSystemAlert(String uniqueKey, String message) {
        // Szukamy, czy nie mamy już OTWARTEGO alertu systemowego
        Optional<GlobalProblem> existingProblem = problemRepository.findByUniqueKey(uniqueKey)
                .filter(p -> !"Done".equals(p.getStatus()));

        if (existingProblem.isEmpty()) {
            GlobalProblem problem = new GlobalProblem();
            problem.setUniqueKey(uniqueKey);
            problem.setSubject("Błąd połączenia z Zabbix API");
            problem.setSource("Local System");
            // Ucinamy wiadomość, jeśli błąd stosu jest za długi
            problem.setContent(message.length() > 255 ? message.substring(0, 252) + "..." : message);
            problem.setStatus("Sent");
            problem.setSeverity(5); // Zakładamy najwyższy priorytet dla błędu całego systemu
            problem.setCreatedAt(LocalDateTime.now());

            GlobalProblem saved = problemRepository.save(problem);
            sseService.sendAlert("NEW_ALERT", saved);
        }
    }

    private void resolveSystemAlert(String uniqueKey) {
        // Zamykamy alert systemowy, jeśli jakiś jest otwarty
        Optional<GlobalProblem> existingProblem = problemRepository.findByUniqueKey(uniqueKey)
                .filter(p -> !"Done".equals(p.getStatus()));

        existingProblem.ifPresent(problem -> {
            problem.setStatus("Done");
            problem.setClosedAt(LocalDateTime.now());
            problemRepository.save(problem);

            sseService.sendAlert("ALERT_RESOLVED", problem);
            System.out.println("[ZABBIX SYNC] Odzyskano połączenie z Zabbixem. Alert usunięty.");
        });
    }
}