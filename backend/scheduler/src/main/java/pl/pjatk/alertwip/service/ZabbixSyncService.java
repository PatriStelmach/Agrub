package pl.pjatk.alertwip.service;

import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ZabbixSyncService implements SchedulingConfigurer {

    private final ZabbixApiService zabbixApiService;
    private final GlobalProblemRepository problemRepository;
    private final SseNotifService sseService;
    private final SystemSettingService settingService;
    private final AlertRoutingService routingService;
    private final ActiveAlertCache alertCache;
    private static final String API_ERROR_UNIQUE_KEY = "[SYSTEM] Zabbix API Offline";

    public ZabbixSyncService(ZabbixApiService zabbixApiService,
                             GlobalProblemRepository problemRepository,
                             SseNotifService sseService,
                             SystemSettingService settingService,
                             AlertRoutingService routingService,
                             ActiveAlertCache alertCache) {
        this.zabbixApiService = zabbixApiService;
        this.problemRepository = problemRepository;
        this.sseService = sseService;
        this.settingService = settingService;
        this.routingService = routingService;
        this.alertCache = alertCache;
    }

    // ==========================================
    // DYNAMICZNY SCHEDULER BAZUJĄCY NA BAZIE DANYCH
    // ==========================================
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                this::syncAlerts, // Metoda do wykonania
                triggerContext -> {
                    Map<String, String> settings = settingService.getAllSettings();
                    String timerValue = settings.getOrDefault("external_system_sync_timer", "60");

                    long intervalMs;
                    try {
                        intervalMs = Long.parseLong(timerValue) * 1000L;
                    } catch (NumberFormatException e) {
                        intervalMs = 60000L; // Fallback na 60 sekund w razie błędnej wartości w bazie
                    }

                    return Instant.now().plusMillis(intervalMs);
                }
        );
    }


    @Transactional
    public void syncAlerts() {
        if (!settingService.getBoolean("zabbix_enabled", false)) {
            return;
        }

        List<Map<String, Object>> currentProblems;

        try {
            currentProblems = zabbixApiService.getActiveProblems();
            if (currentProblems == null) {
                throw new RuntimeException("Zabbix API zwróciło null.");
            }
        } catch (Exception e) {
            System.err.println("[ZABBIX SYNC] Awaria połączenia API. Generuję alert systemowy...");
            triggerSystemAlert(API_ERROR_UNIQUE_KEY, "Brak komunikacji z Zabbix API: " + e.getMessage());
            return;
        }

        resolveSystemAlert(API_ERROR_UNIQUE_KEY);
        System.out.println("[ZABBIX SYNC] Synchronizacja. Aktywne problemy w Zabbix: " + currentProblems.size());

        // Pobieramy wszystkie aktualnie "Otwarte" problemy z Zabbixa
        List<GlobalProblem> activeDbZabbixProblems = problemRepository.findByOriginTypeAndStatusNot("ZABBIX", "Done");

        Map<String, GlobalProblem> existingProblemsMap = activeDbZabbixProblems.stream()
                .collect(Collectors.toMap(GlobalProblem::getUniqueKey, Function.identity(), (existing, replacement) -> existing));

        Set<String> activeZabbixProblemKeys = new HashSet<>();

        // 2. PRZETWARZANIE ALERTÓW Z API
        for (Map<String, Object> problemData : currentProblems) {

            Object nameObj = problemData.get("name");
            String name = (nameObj != null) ? nameObj.toString() : "Nieznany błąd";

            String zabbixEventId = null;
            Object eventIdObj = problemData.get("eventid");
            if (eventIdObj != null) {
                zabbixEventId = eventIdObj.toString();
            }

            int severity = 0;
            Object severityObj = problemData.get("severity");
            if (severityObj != null) {
                try { severity = Integer.parseInt(severityObj.toString()); } catch (NumberFormatException ignored) {}
            }

            String hostName = "Nieznany host";
            Object hostsObj = problemData.get("hosts");
            if (hostsObj instanceof List<?> hostList && !hostList.isEmpty()) {
                if (hostList.get(0) instanceof Map<?, ?> hostMap && hostMap.get("name") != null) {
                    hostName = hostMap.get("name").toString();
                }
            }

            String uniqueProblemKey = "[ZABBIX] " + hostName + " - " + name;
            activeZabbixProblemKeys.add(uniqueProblemKey);

            // Sprawdzamy w lokalnej mapie
            GlobalProblem existingProblem = existingProblemsMap.get(uniqueProblemKey);

            if (existingProblem == null) {
                // TWORZENIE NOWEGO ALERTU
                GlobalProblem newProblem = new GlobalProblem();
                newProblem.setUniqueKey(uniqueProblemKey);
                newProblem.setSubject(name);
                newProblem.setSource(hostName);
                newProblem.setOriginType("ZABBIX");
                newProblem.setExternalEventId(zabbixEventId);
                newProblem.setMessage("Problem zsynchronizowany z API Zabbix.");
                newProblem.setStatus("Sent");
                newProblem.setSeverity(severity);
                newProblem.setCreatedAt(LocalDateTime.now());

                routingService.processVisibility(newProblem);
                GlobalProblem saved = problemRepository.save(newProblem);
                alertCache.updateAlert(saved);
                sseService.sendAlert("NEW_ALERT", saved);

            } else {
                // AKTUALIZACJA ISTNIEJĄCEGO
                if (existingProblem.getSeverity() != severity) {
                    System.out.println("[ZABBIX SYNC] Wykryto zmianę severity dla: " + uniqueProblemKey + " (" + existingProblem.getSeverity() + " -> " + severity + ")");
                    existingProblem.setSeverity(severity);
                    GlobalProblem updated = problemRepository.save(existingProblem);

                    alertCache.updateAlert(updated);
                    sseService.sendAlert("ALERT_UPDATE", updated);
                }
            }
        }

        // 3. ZAMYKANIE STARYCH ALERTÓW
        for (GlobalProblem dbProblem : activeDbZabbixProblems) {
            // Ignorujemy nasz alert systemowy, żeby go przypadkiem nie zamknąć Zabbixem
            if (API_ERROR_UNIQUE_KEY.equals(dbProblem.getUniqueKey())) continue;

            if (!activeZabbixProblemKeys.contains(dbProblem.getUniqueKey())) {
                dbProblem.setStatus("Done");
                dbProblem.setClosedAt(LocalDateTime.now());
                problemRepository.save(dbProblem);
                alertCache.removeAlert(dbProblem.getId());
                sseService.sendAlert("ALERT_RESOLVED", dbProblem);
            }
        }
    }

    // ==========================================
    // METODY POMOCNICZE DLA ALERTÓW SYSTEMOWYCH
    // ==========================================

    private void triggerSystemAlert(String uniqueKey, String message) {
        Optional<GlobalProblem> existingProblem = problemRepository.findFirstByUniqueKeyOrderByIdDesc(uniqueKey)
                .filter(p -> !"Done".equals(p.getStatus()));

        if (existingProblem.isEmpty()) {
            GlobalProblem problem = new GlobalProblem();
            problem.setUniqueKey(uniqueKey);
            problem.setSubject("Błąd połączenia z Zabbix API");
            problem.setSource("Local System");
            problem.setOriginType("ZABBIX");
            problem.setMessage(message.length() > 255 ? message.substring(0, 252) + "..." : message);
            problem.setStatus("Sent");
            problem.setSeverity(5);
            problem.setCreatedAt(LocalDateTime.now());
            routingService.processVisibility(problem);

            GlobalProblem saved = problemRepository.save(problem);
            alertCache.updateAlert(saved);
            sseService.sendAlert("NEW_ALERT", saved);
        }
    }

    private void resolveSystemAlert(String uniqueKey) {
        Optional<GlobalProblem> existingProblem = problemRepository.findFirstByUniqueKeyOrderByIdDesc(uniqueKey)
                .filter(p -> !"Done".equals(p.getStatus()));

        existingProblem.ifPresent(problem -> {
            problem.setStatus("Done");
            problem.setClosedAt(LocalDateTime.now());
            problemRepository.save(problem);
            alertCache.removeAlert(problem.getId());
            sseService.sendAlert("ALERT_RESOLVED", problem);
            System.out.println("[ZABBIX SYNC] Odzyskano połączenie z Zabbixem. Alert usunięty.");
        });
    }
}