package pl.pjatk.alertwip.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class NagiosSyncService {

    private final NagiosApiService nagiosApi;
    private final GlobalProblemRepository problemRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AlertRoutingService routingService;
    private final ActiveAlertCache alertCache;
    private final SseNotifService sseService; // Zaktualizowana nazwa!
    private final SystemSettingService settingService;

    public NagiosSyncService(NagiosApiService nagiosApi,
                             GlobalProblemRepository problemRepository,
                             AlertRoutingService routingService,
                             ActiveAlertCache alertCache,
                             SseNotifService sseService,
                             SystemSettingService settingService) {
        this.nagiosApi = nagiosApi;
        this.problemRepository = problemRepository;
        this.routingService = routingService;
        this.alertCache = alertCache;
        this.sseService = sseService;
        this.settingService = settingService;
    }

    @Scheduled(fixedDelay = 30000)
    public void sync() {
        if (!settingService.getBoolean("nagios_enabled", false)) return;

        System.out.println("[NAGIOS SYNC] Pobieranie zdarzeń...");

        try {
            String rawJson = nagiosApi.fetchAlerts();
            if (rawJson == null) return;

            resolveSystemAlert("[SYSTEM] - NAGIOS_API_OFFLINE");

            JsonNode servicelist = mapper.readTree(rawJson).path("data").path("servicelist");
            if (!servicelist.isObject()) return;

            int added = 0;

            for (Map.Entry<String, JsonNode> hostEntry : servicelist.properties()) {
                String hostName = hostEntry.getKey();
                JsonNode services = hostEntry.getValue();

                for (Map.Entry<String, JsonNode> serviceEntry : services.properties()) {
                    String serviceName = serviceEntry.getKey();
                    JsonNode details = serviceEntry.getValue();

                    int status = details.path("status").asInt(0);
                    String output = details.path("plugin_output").asText("Brak opisu awarii");
                    boolean acknowledged = details.path("has_been_acknowledged").asBoolean(false);

                    if (acknowledged) continue;

                    String uniqueKey = "[NAGIOS] - " + hostName + " - " + serviceName;

                    if (!problemRepository.existsByUniqueKey(uniqueKey)) {
                        GlobalProblem problem = new GlobalProblem();
                        problem.setUniqueKey(uniqueKey);
                        problem.setSubject("Nagios: " + serviceName);
                        problem.setSource(hostName);
                        problem.setOriginType("NAGIOS");
                        problem.setMessage(output.length() > 255 ? output.substring(0, 252) + "..." : output);

                        problem.setSeverity(status == 2 ? 5 : (status == 1 ? 3 : 2));
                        problem.setStatus("Sent");
                        problem.setCreatedAt(LocalDateTime.now());

                        routingService.processVisibility(problem);

                        GlobalProblem saved = problemRepository.save(problem);
                        alertCache.updateAlert(saved);
                        sseService.sendAlert("NEW_ALERT", saved);
                        added++;
                    }
                }
            }

            if (added > 0) {
                System.out.println("[NAGIOS SYNC] Zapisano " + added + " nowych alertów z Nagiosa do bazy!");
            }

        } catch (Exception e) {
            System.err.println("[NAGIOS SYNC] Błąd pobierania alertów: " + e.getMessage());
            generateSystemAlert("[SYSTEM] - NAGIOS_API_OFFLINE", "[NAGIOS SYNC] Awaria połączenia z API. Generuję alert systemowy...");
        }
    }

    private void generateSystemAlert(String uniqueKey, String message) {
        if (!problemRepository.existsByUniqueKey(uniqueKey)) {
            System.out.println(message);
            GlobalProblem problem = new GlobalProblem();
            problem.setUniqueKey(uniqueKey);
            problem.setSubject("System Alert: Nagios API Offline");
            problem.setSource("AlertWIP System");
            problem.setOriginType("NAGIOS");
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
            System.out.println("[NAGIOS SYNC] Odzyskano połączenie z Nagiosem. Alert usunięty ze strumienia.");
        });
    }
}