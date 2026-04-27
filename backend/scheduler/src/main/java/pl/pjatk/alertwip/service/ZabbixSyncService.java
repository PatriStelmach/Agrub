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
    private final AlertRoutingService routingService; // NOWOŚĆ: Nasz Router Regexów

    // Klucz dla naszego wewnętrznego alertu o awarii komunikacji z Zabbixem
    private static final String API_ERROR_UNIQUE_KEY = "[SYSTEM] Zabbix API Offline";

    public ZabbixSyncService(ZabbixApiService zabbixApiService,
                             GlobalProblemRepository problemRepository,
                             SseNotifService sseService,
                             SystemSettingService settingService,
                             AlertRoutingService routingService) {
        this.zabbixApiService = zabbixApiService;
        this.problemRepository = problemRepository;
        this.sseService = sseService;
        this.settingService = settingService;
        this.routingService = routingService;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void syncAlerts() {
        if (!settingService.getBoolean("zabbix_enabled", false)) {
            return;
        }

        List<Map<String, Object>> currentProblems;

        // 1. ZWĘŻONY TRY-CATCH: Łapiemy tylko i wyłącznie błędy z API Zabbixa
        try {
            currentProblems = zabbixApiService.getActiveProblems();
            if (currentProblems == null) {
                throw new RuntimeException("Zabbix API zwróciło null.");
            }
        } catch (Exception e) {
            System.err.println("[ZABBIX SYNC] Awaria połączenia API. Generuję alert systemowy...");
            triggerSystemAlert(API_ERROR_UNIQUE_KEY, "Brak komunikacji z Zabbix API: " + e.getMessage());
            return; // Przewany proces, nie idziemy dalej!
        }

        // Jeśli tu doszliśmy, Zabbix API działa w 100%.
        resolveSystemAlert(API_ERROR_UNIQUE_KEY);
        System.out.println("[ZABBIX SYNC] Synchronizacja. Aktywne problemy w Zabbix: " + currentProblems.size());

        Set<String> activeZabbixProblemKeys = new HashSet<>();

        // 2. PRZETWARZANIE ALERTÓW (Bez obaw, że błąd SSE wywoła awarię Zabbixa)
        for (Map<String, Object> problemData : currentProblems) {
            Object nameObj = problemData.get("name");
            String name = (nameObj != null) ? nameObj.toString() : "Nieznany błąd";

            int severity = 0;
            Object severityObj = problemData.get("severity");
            if (severityObj != null) {
                try { severity = Integer.parseInt(severityObj.toString()); } catch (NumberFormatException ignored) {}
            }

            String hostName = "Nieznany host";
            Object hostsObj = problemData.get("hosts");
            if (hostsObj instanceof List<?> hostList && !hostList.isEmpty()) {
                if (hostList.get(0) instanceof Map<?, ?> hostMap && hostMap.get("host") != null) {
                    hostName = hostMap.get("host").toString();
                }
            }

            String uniqueProblemKey = "[ZABBIX] " + hostName + " - " + name;
            activeZabbixProblemKeys.add(uniqueProblemKey);

            // Używamy nowej metody zabezpieczającej przed duplikatami!
            Optional<GlobalProblem> existingProblem = problemRepository.findFirstByUniqueKeyOrderByIdDesc(uniqueProblemKey)
                    .filter(p -> !"Done".equals(p.getStatus()));

            if (existingProblem.isEmpty()) {
                GlobalProblem newProblem = new GlobalProblem();
                newProblem.setUniqueKey(uniqueProblemKey);
                newProblem.setSubject(name);
                newProblem.setSource(hostName);     // konkretna maszyna
                newProblem.setOriginType("ZABBIX");
                newProblem.setMessage("Problem zsynchronizowany z API Zabbix.");
                newProblem.setStatus("Sent");
                newProblem.setSeverity(severity);
                newProblem.setCreatedAt(LocalDateTime.now());

                routingService.processVisibility(newProblem);
                GlobalProblem saved = problemRepository.save(newProblem);

                sseService.sendAlert("NEW_ALERT", saved);
            }
        }

        // 3. ZAMYKANIE STARYCH ALERTÓW
        List<GlobalProblem> dbZabbixProblems = problemRepository.findAll().stream()
                .filter(p -> p.getUniqueKey() != null && p.getUniqueKey().startsWith("[ZABBIX]"))
                .filter(p -> !"Done".equals(p.getStatus()))
                .toList();

        for (GlobalProblem dbProblem : dbZabbixProblems) {
            if (!activeZabbixProblemKeys.contains(dbProblem.getUniqueKey())) {
                dbProblem.setStatus("Done");
                dbProblem.setClosedAt(LocalDateTime.now());
                problemRepository.save(dbProblem);
                sseService.sendAlert("ALERT_RESOLVED", dbProblem);
            }
        }
    }


    // ==========================================
    // METODY POMOCNICZE DLA ALERTÓW SYSTEMOWYCH
    // ==========================================

    private void triggerSystemAlert(String uniqueKey, String message) {
        // Szukamy, czy nie mamy już OTWARTEGO alertu systemowego
        Optional<GlobalProblem> existingProblem = problemRepository.findFirstByUniqueKeyOrderByIdDesc(uniqueKey)
                .filter(p -> !"Done".equals(p.getStatus()));

        if (existingProblem.isEmpty()) {
            GlobalProblem problem = new GlobalProblem();
            problem.setUniqueKey(uniqueKey);
            problem.setSubject("Błąd połączenia z Zabbix API");
            problem.setSource("Local System");
            problem.setOriginType("ZABBIX");
            // Ucinamy wiadomość, jeśli błąd stosu jest za długi
            problem.setMessage(message.length() > 255 ? message.substring(0, 252) + "..." : message);
            problem.setStatus("Sent");
            problem.setSeverity(5); // Zakładamy najwyższy priorytet dla błędu całego systemu
            problem.setCreatedAt(LocalDateTime.now());

            // --- MAGIA SILNIKA REGUŁ DLA ALERTU SYSTEMOWEGO ---
            routingService.processVisibility(problem);

            GlobalProblem saved = problemRepository.save(problem);
            sseService.sendAlert("NEW_ALERT", saved);
        }
    }

    private void resolveSystemAlert(String uniqueKey) {
        // Zamykamy alert systemowy, jeśli jakiś jest otwarty
        Optional<GlobalProblem> existingProblem = problemRepository.findFirstByUniqueKeyOrderByIdDesc(uniqueKey)
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