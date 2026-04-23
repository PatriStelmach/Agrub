package pl.pjatk.alertwip.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.service.SystemSettingService;

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

    // Nazwa dla naszego wewnętrznego alertu o awarii komunikacji z Zabbixem
    private static final String API_ERROR_TASK_NAME = "[SYSTEM] Zabbix API Offline";

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
            // Zabbix jest wyłączony, spierdalamy
            return;
        }
            try {
                // Próba pobrania danych z API Zabbixa
                List<Map<String, Object>> currentProblems = zabbixApiService.getActiveProblems();

                if (currentProblems == null) {
                    // Jeśli zwraca null, traktujemy to jako błąd komunikacji i zrzucamy do catcha
                    throw new RuntimeException("Zabbix API zwróciło pustą odpowiedź (null).");
                }
                resolveSystemAlert(API_ERROR_TASK_NAME);

                System.out.println("[ZABBIX SYNC] Synchronizacja. Aktywne problemy w Zabbix: " + currentProblems.size());

                // 1. Zbieramy unikalne nazwy trwających problemów do późniejszego porównania
                Set<String> activeZabbixProblemNames = new HashSet<>();

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

                    // Budujemy klucz identyczny z tym z Webhooka (łączymy host i problem)
                    String uniqueProblemName = "[ZABBIX] " + hostName + " - " + name;
                    activeZabbixProblemNames.add(uniqueProblemName);

                    // 2. SZUKANIE ZGUBIONYCH ALERTÓW (Są w Zabbixie, a nie ma ich w naszej bazie)
                    Optional<GlobalProblem> existingProblem = problemRepository.findByTaskName(uniqueProblemName);
                    if (existingProblem.isEmpty()) {
                        GlobalProblem newProblem = new GlobalProblem();
                        newProblem.setTaskName(uniqueProblemName);

                        // CZYSTA WIADOMOŚĆ (bez doklejania severity)
                        newProblem.setLastErrorMessage("Problem dodany przez Sync API.");

                        // USTAWIAMY OSOBNE POLE SEVERITY
                        newProblem.setSeverity(severity);

                        newProblem.setOccurrenceTime(LocalDateTime.now());

                        GlobalProblem saved = problemRepository.save(newProblem);

                        sseService.sendAlert("NEW_ALERT", saved);
                        System.out.println("[ZABBIX SYNC] Dodano zgubiony alert: " + uniqueProblemName + " (Severity: " + severity + ")");
                    }
                }

                // 3. SZUKANIE ZGUBIONYCH ROZWIĄZAŃ (Są w naszej bazie, ale zniknęły już z Zabbixa)
                List<GlobalProblem> dbZabbixProblems = problemRepository.findAll().stream()
                        .filter(p -> p.getTaskName() != null && p.getTaskName().startsWith("[ZABBIX]"))
                        .toList();

                for (GlobalProblem dbProblem : dbZabbixProblems) {
                    // Jeśli problem z bazy NIE ZNAJDUJE SIĘ już na liście pobranej z Zabbixa przed chwilą...
                    if (!activeZabbixProblemNames.contains(dbProblem.getTaskName())) {
                        problemRepository.delete(dbProblem);

                        // ...to znaczy, że został rozwiązany. Informujemy Vue!
                        sseService.sendAlert("ALERT_RESOLVED", dbProblem);
                        System.out.println("[ZABBIX SYNC] Wyczyszczono rozwiązany alert: " + dbProblem.getTaskName());
                    }
                }

            } catch (Exception e) {
                // --- AWARIA POŁĄCZENIA ---
                // Zamiast wywalać aplikację, drukujemy ostrzeżenie w konsoli
                System.err.println("[ZABBIX SYNC] Awaria połączenia. Generuję alert systemowy dla Vue...");

                // Wysyłamy alert o awarii na nasz własny frontend
                triggerSystemAlert(API_ERROR_TASK_NAME, "Brak komunikacji z Zabbix API: " + e.getMessage());
            }
        }


        // ==========================================
        // METODY POMOCNICZE DLA ALERTÓW SYSTEMOWYCH
        // ==========================================

        private void triggerSystemAlert (String taskName, String message){
            Optional<GlobalProblem> existingProblem = problemRepository.findByTaskName(taskName);

            if (existingProblem.isEmpty()) {
                GlobalProblem problem = new GlobalProblem();
                problem.setTaskName(taskName);
                // Ucinamy wiadomość, jeśli błąd stosu jest dłuższy niż pojemność kolumny w MySQL (255 znaków)
                problem.setLastErrorMessage(message.length() > 255 ? message.substring(0, 252) + "..." : message);
                problem.setOccurrenceTime(LocalDateTime.now());

                GlobalProblem saved = problemRepository.save(problem);
                // Informujemy frontend o problemie z infrastrukturą
                sseService.sendAlert("NEW_ALERT", saved);
            }
        }

        private void resolveSystemAlert (String taskName){
            Optional<GlobalProblem> existingProblem = problemRepository.findByTaskName(taskName);

            existingProblem.ifPresent(problem -> {
                problemRepository.delete(problem);
                // Informujemy frontend, że infrastruktura znowu działa
                sseService.sendAlert("ALERT_RESOLVED", problem);
                System.out.println("[ZABBIX SYNC] Odzyskano połączenie z Zabbixem. Alert usunięty.");
            });
        }
    }