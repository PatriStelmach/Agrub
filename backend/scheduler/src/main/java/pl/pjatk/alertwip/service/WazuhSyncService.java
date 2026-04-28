package pl.pjatk.alertwip.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class WazuhSyncService {

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

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void sync() {
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
            //Zgłaszanie awarii integracji do frontendu!
            System.err.println("[WAZUH SYNC] Awaria połączenia API. Generuję alert systemowy...");
            triggerSystemAlert(API_ERROR_UNIQUE_KEY, "Brak komunikacji z Wazuh API: " + e.getMessage());
            return;
        }

        // Usunięcie ewentualnego alertu systemowego, bo połączenie wróciło
        resolveSystemAlert(API_ERROR_UNIQUE_KEY);

        try {
            JsonNode logs = mapper.readTree(rawJson).path("data").path("affected_items");

            if (logs.isArray() && !logs.isEmpty()) {
                int added = 0;

                Set<String> existingKeys = problemRepository.findAllWazuhUniqueKeys();

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

                        problem.setStatus("New");
                        problem.setCreatedAt(LocalDateTime.now());

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