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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NagiosSyncService implements SchedulingConfigurer {

    private final NagiosApiService nagiosApi;
    private final GlobalProblemRepository problemRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AlertRoutingService routingService;
    private final ActiveAlertCache alertCache;
    private final SseNotifService sseService;
    private final SystemSettingService settingService;

    private static final String API_ERROR_UNIQUE_KEY = "[SYSTEM] - NAGIOS_API_OFFLINE";

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

    @Transactional
    public void sync() {
        if (!settingService.getBoolean("nagios_enabled", false)) return;

        System.out.println("[NAGIOS SYNC] Pobieranie zdarzeń...");

        String rawJson;
        try {
            rawJson = nagiosApi.fetchAlerts();
            if (rawJson == null) throw new RuntimeException("API zwróciło pustą odpowiedź");
        } catch (Exception e) {
            System.err.println("[NAGIOS SYNC] Błąd połączenia API. Generuję alert systemowy...");
            generateSystemAlert(API_ERROR_UNIQUE_KEY, "Awaria komunikacji z Nagios API: " + e.getMessage());
            return;
        }

        resolveSystemAlert(API_ERROR_UNIQUE_KEY);

        try {
            JsonNode servicelist = mapper.readTree(rawJson).path("data").path("servicelist");
            if (!servicelist.isObject()) return;

            List<GlobalProblem> activeDbNagiosProblems = problemRepository.findByOriginTypeAndStatusNot("NAGIOS", "Done");
            Map<String, GlobalProblem> existingProblemsMap = activeDbNagiosProblems.stream()
                    .collect(Collectors.toMap(GlobalProblem::getUniqueKey, Function.identity(), (existing, replacement) -> existing));

            Set<String> activeNagiosProblemKeys = new HashSet<>();
            int added = 0;
            int updatedCount = 0;

            for (Map.Entry<String, JsonNode> hostEntry : servicelist.properties()) {
                String hostName = hostEntry.getKey();
                JsonNode services = hostEntry.getValue();

                for (Map.Entry<String, JsonNode> serviceEntry : services.properties()) {
                    String serviceName = serviceEntry.getKey();
                    JsonNode details = serviceEntry.getValue();

                    boolean acknowledged = details.path("has_been_acknowledged").asBoolean(false);
                    // if (acknowledged) continue; // TODO: dodać opcje znikania ackowanych z nagiosa

                    int status = details.path("status").asInt(0);
                    int severity = switch (status) {
                        case 16 -> 5; // CRITICAL
                        case 4  -> 3; // WARNING
                        case 8  -> 2; // UNKNOWN
                        default -> 2;
                    };

                    String output = details.path("plugin_output").asText("Brak opisu awarii");
                    String uniqueKey = "[NAGIOS] - " + hostName + " - " + serviceName;

                    activeNagiosProblemKeys.add(uniqueKey);

                    // Sprawdzamy w mapie w pamięci RAM
                    GlobalProblem existingProblem = existingProblemsMap.get(uniqueKey);

                    if (existingProblem == null) {
                        // TWORZENIE NOWEGO
                        GlobalProblem problem = new GlobalProblem();
                        problem.setUniqueKey(uniqueKey);
                        problem.setSubject(hostName + ": " + serviceName);
                        problem.setSource(hostName);
                        problem.setOriginType("NAGIOS");
                        problem.setMessage(output.length() > 255 ? output.substring(0, 252) + "..." : output);
                        problem.setExternalEventId(serviceName); // Ważne do CGI ACK
                        problem.setSeverity(severity);
                        problem.setStatus("Sent");
                        problem.setCreatedAt(LocalDateTime.now());

                        routingService.processVisibility(problem);

                        GlobalProblem saved = problemRepository.save(problem);
                        alertCache.updateAlert(saved);
                        sseService.sendAlert("NEW_ALERT", saved);
                        added++;
                    } else {
                        // AKTUALIZACJA SEVERITY
                        if (existingProblem.getSeverity() != severity) {
                            System.out.println("[NAGIOS SYNC] Zmiana severity dla " + uniqueKey + " (" + existingProblem.getSeverity() + " -> " + severity + ")");
                            existingProblem.setSeverity(severity);
                            GlobalProblem updated = problemRepository.save(existingProblem);

                            alertCache.updateAlert(updated);
                            sseService.sendAlert("ALERT_UPDATE", updated);
                            updatedCount++;
                        }
                    }
                }
            }

            // ZAMYKANIE STARYCH
            int resolved = 0;
            for (GlobalProblem dbProblem : activeDbNagiosProblems) {
                if (API_ERROR_UNIQUE_KEY.equals(dbProblem.getUniqueKey())) continue;

                if (!activeNagiosProblemKeys.contains(dbProblem.getUniqueKey())) {
                    dbProblem.setStatus("Done");
                    dbProblem.setClosedAt(LocalDateTime.now());
                    problemRepository.save(dbProblem);
                    alertCache.removeAlert(dbProblem.getId());
                    sseService.sendAlert("ALERT_RESOLVED", dbProblem);
                    resolved++;
                }
            }

            if (added > 0 || resolved > 0 || updatedCount > 0) {
                System.out.println(String.format("[NAGIOS SYNC] Synchronizacja zakończona: Dodano %d, Zaktualizowano %d, Zamknięto %d", added, updatedCount, resolved));
            }

        } catch (Exception e) {
            System.err.println("[NAGIOS SYNC] Błąd mapowania danych API: " + e.getMessage());
        }
    }

    private void generateSystemAlert(String uniqueKey, String message) {
        Optional<GlobalProblem> existingProblem = problemRepository.findFirstByUniqueKeyOrderByIdDesc(uniqueKey)
                .filter(p -> !"Done".equals(p.getStatus()));

        if (existingProblem.isEmpty()) {
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