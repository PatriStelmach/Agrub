package pl.pjatk.alertwip.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class WazuhSyncService implements SchedulingConfigurer {

    private final WazuhApiService wazuhApi;
    private final GlobalProblemRepository problemRepository;
    private final ActiveAlertCache alertCache;
    private final SseNotifService sseService;
    private final AlertRoutingService routingService;
    private final SystemSettingService settingService;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String API_ERROR_UNIQUE_KEY = "[SYSTEM] Wazuh API Offline";

    public WazuhSyncService(WazuhApiService wazuhApi,
                            GlobalProblemRepository problemRepository,
                            ActiveAlertCache alertCache,
                            SseNotifService sseService,
                            AlertRoutingService routingService,
                            SystemSettingService settingService) {
        this.wazuhApi = wazuhApi;
        this.problemRepository = problemRepository;
        this.alertCache = alertCache;
        this.sseService = sseService;
        this.routingService = routingService;
        this.settingService = settingService;
    }


    // WYŁĄCZONE
    // Idk czy chcemy to zrobić, lepiej chyba żeby tylko na bieżąco przychodziło?
    // w wazuhu nie ma jako tako aktywnych alertów tylko po prostu logi więc trzeba by porównywać stan bazy aktualny całej
    // ze stanem całego wazuha a to będzie duża operacja
    // może dać to ale tego synca raz na godzinę?
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                this::sync,
                triggerContext -> {
                    Map<String, String> settings = settingService.getAllSettings();
                    String timerValue = settings.getOrDefault("external_system_sync_timer", "60");

                    long intervalMs;
                    try {
                        intervalMs = Long.parseLong(timerValue) * 1000L;
                    } catch (NumberFormatException e) {
                        intervalMs = 60000L;
                    }

                    return Instant.now().plusMillis(intervalMs);
                }
        );
    }

    // WYŁĄCZONE TUTAJ
    @Transactional
    public void sync() {

        return;

        /*
        if (!settingService.getBoolean("wazuh_enabled", false)) {
            return;
        }

        System.out.println("[WAZUH SYNC] Pobieranie logów operacyjnych...");
        String token;
        String rawJson;

        try {
            token = wazuhApi.getAuthToken();
            if (token == null) {
                throw new RuntimeException("Brak tokenu autoryzacyjnego Wazuh.");
            }
            rawJson = wazuhApi.fetchAlerts(token);
            if (rawJson == null) {
                throw new RuntimeException("Wazuh API zwróciło null podczas pobierania logów.");
            }
        } catch (Exception e) {
            System.err.println("[WAZUH SYNC] Awaria połączenia API. Generuję alert systemowy...");
            triggerSystemAlert(API_ERROR_UNIQUE_KEY, "Brak komunikacji z Wazuh API: " + e.getMessage());
            return;
        }

        resolveSystemAlert(API_ERROR_UNIQUE_KEY);

        try {
            JsonNode logs = mapper.readTree(rawJson).path("data").path("affected_items");

            if (logs.isArray() && !logs.isEmpty()) {
                int added = 0;

                Set<String> existingKeys = problemRepository.findAllWazuhUniqueKeys();

                int minActiveLevel;
                try {
                    minActiveLevel = Integer.parseInt(settingService.getValue("wazuh_min_active_level", "8"));
                } catch (Exception e) {
                    minActiveLevel = 8;
                }

                for (JsonNode logNode : logs) {
                    String timestamp = logNode.path("timestamp").asText("");
                    String tag = logNode.path("tag").asText("");
                    String level = logNode.path("level").asText("");
                    String description = logNode.path("description").asText("");

                    String logText = String.format("[%s] %s (%s): %s", timestamp, tag, level, description);

                    if (logText.trim().length() < 10) continue;

                    String uniqueKey = "[WAZUH] - Hash: " + logText.hashCode();

                    if (!existingKeys.contains(uniqueKey)) {
                        GlobalProblem problem = new GlobalProblem();
                        problem.setUniqueKey(uniqueKey);
                        problem.setSubject("Wazuh Raw Log");
                        problem.setSource(logText.contains("prod-agent-01") ? "prod-agent-01" : "wazuh-manager");
                        problem.setOriginType("WAZUH");
                        problem.setMessage(logText);

                        if (logText.toUpperCase().contains("ERROR") || logText.toUpperCase().contains("CRITICAL")) {
                            problem.setSeverity(4);
                        } else if(logText.toUpperCase().contains("WARNING")) {
                            problem.setSeverity(3);
                        } else {
                            problem.setSeverity(2);
                        }

                        int wazuhRuleLevel = 0;
                        try {
                            wazuhRuleLevel = Integer.parseInt(level);
                        } catch (NumberFormatException ignored) {}

                        if (wazuhRuleLevel > 0 && wazuhRuleLevel < minActiveLevel) {
                            problem.setStatus("Done");
                            problem.setAcknowledged(true);
                            problem.setClosedAt(LocalDateTime.now());

                            problemRepository.save(problem);
                            existingKeys.add(uniqueKey);
                            added++;
                            continue;
                        }

                        problem.setStatus("New");
                        routingService.processVisibility(problem);

                        GlobalProblem saved = problemRepository.save(problem);
                        alertCache.updateAlert(saved);
                        sseService.sendAlert("NEW_ALERT", saved);

                        existingKeys.add(uniqueKey);
                        added++;
                    }
                }

                if (added > 0) {
                    System.out.println("[WAZUH SYNC] Zapisano " + added + " logów i powiadomiono frontend!");
                }
            }
        } catch (Exception e) {
            System.err.println("[WAZUH SYNC] Błąd parsowania: " + e.getMessage());
            e.printStackTrace();
        }
         */
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
            problem.setSubject("Błąd połączenia z Wazuh API");
            problem.setSource("Local System");
            problem.setOriginType("WAZUH");
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
            System.out.println("[WAZUH SYNC] Odzyskano połączenie z Wazuh. Alert usunięty.");
        });
    }
}