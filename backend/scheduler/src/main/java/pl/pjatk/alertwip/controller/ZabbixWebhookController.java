package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.service.ActiveAlertCache;
import pl.pjatk.alertwip.service.AlertRoutingService;
import pl.pjatk.alertwip.service.SseNotifService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks")
@PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_API_CLIENT')")
public class ZabbixWebhookController {

    private final GlobalProblemRepository problemRepository;
    private final SseNotifService sseService;
    private final AlertRoutingService routingService;
    private final ActiveAlertCache alertCache;

    public ZabbixWebhookController(GlobalProblemRepository problemRepository,
                                   SseNotifService sseService,
                                   AlertRoutingService routingService,
                                   ActiveAlertCache alertCache) {
        this.problemRepository = problemRepository;
        this.sseService = sseService;
        this.routingService = routingService;
        this.alertCache = alertCache;
    }

    @PostMapping("/zabbix/alert")
    public ResponseEntity<String> handleZabbixAlert(@RequestBody Map<String, Object> payload) {
        String eventStatus = (String) payload.getOrDefault("status", "PROBLEM");
        String eventName = (String) payload.getOrDefault("name", "Nieznany błąd");
        String host = (String) payload.getOrDefault("host", "Nieznany host");

        String eventId = null;
        Object eventIdObj = payload.get("eventid");
        if (eventIdObj != null) {
            eventId = eventIdObj.toString();
        }

        int severity = 0;
        Object sevObj = payload.get("severity");
        if (sevObj != null) {
            try { severity = Integer.parseInt(sevObj.toString()); } catch (NumberFormatException ignored) {}
        }

        String uniqueProblemKey = "[ZABBIX] " + host + " - " + eventName;

        if (eventStatus.equalsIgnoreCase("PROBLEM")) {
            Optional<GlobalProblem> existing = problemRepository.findFirstByUniqueKeyOrderByIdDesc(uniqueProblemKey)
                    .filter(p -> !"Done".equals(p.getStatus()));

            if (existing.isEmpty()) {
                GlobalProblem problem = new GlobalProblem();
                problem.setUniqueKey(uniqueProblemKey);
                problem.setSubject(eventName);
                problem.setSource(host);
                problem.setOriginType("ZABBIX");
                problem.setMessage("Alert odebrany z Zabbixa z hosta: " + host);
                problem.setStatus("Sent");
                problem.setSeverity(severity);
                problem.setCreatedAt(LocalDateTime.now());
                problem.setExternalEventId(eventId);

                // Procesowanie widoczności przed zapisem
                routingService.processVisibility(problem);

                GlobalProblem cachedProblem = problemRepository.save(problem);
                alertCache.updateAlert(cachedProblem);
                sseService.sendAlert("NEW_ALERT", cachedProblem);
            }

        } else if (eventStatus.equalsIgnoreCase("OK") || eventStatus.equalsIgnoreCase("RESOLVED")) {
            Optional<GlobalProblem> existing = problemRepository.findFirstByUniqueKeyOrderByIdDesc(uniqueProblemKey)
                    .filter(p -> !"Done".equals(p.getStatus()));

            existing.ifPresent(problem -> {
                problem.setStatus("Done");
                problem.setClosedAt(LocalDateTime.now());

                GlobalProblem cachedProblem = problemRepository.save(problem);
                alertCache.removeAlert(cachedProblem.getId());
                sseService.sendAlert("ALERT_RESOLVED", cachedProblem);
            });
        }

        return ResponseEntity.ok("Zabbix Alert processed");
    }

    @PostMapping("/nagios")
    public ResponseEntity<String> handleNagiosAlert(@RequestBody Map<String, Object> payload) {
        String eventStatus = (String) payload.getOrDefault("status", "PROBLEM");
        String eventName = (String) payload.getOrDefault("name", "Nieznany błąd Nagios");
        String host = (String) payload.getOrDefault("host", "Nieznany host");
        String eventId = (String) payload.get("externalEventId");
        String message = (String) payload.getOrDefault("message", "Brak szczegółowego opisu z Nagiosa");

        String severityStr = (String) payload.getOrDefault("severity", "UNKNOWN");
        int severity = mapNagiosSeverityToLevel(severityStr);

        String uniqueProblemKey = "[NAGIOS] - " + host + " - " + (eventId != null ? eventId : eventName);


        if (eventStatus.equalsIgnoreCase("PROBLEM")) {
            Optional<GlobalProblem> existing = problemRepository.findFirstByUniqueKeyOrderByIdDesc(uniqueProblemKey)
                    .filter(p -> !"Done".equals(p.getStatus()));

            if (existing.isEmpty()) {
                GlobalProblem problem = new GlobalProblem();
                problem.setUniqueKey(uniqueProblemKey);
                problem.setSubject(eventName);
                problem.setSource(host);
                problem.setOriginType("NAGIOS");
                problem.setMessage(message);
                problem.setStatus("Sent");
                problem.setSeverity(severity);
                problem.setCreatedAt(LocalDateTime.now());
                problem.setExternalEventId(eventId);

                routingService.processVisibility(problem);

                GlobalProblem saved = problemRepository.save(problem);
                alertCache.updateAlert(saved);
                sseService.sendAlert("NEW_ALERT", saved);
            }

        } else if (eventStatus.equalsIgnoreCase("OK") || eventStatus.equalsIgnoreCase("RESOLVED")) {
            Optional<GlobalProblem> existing = problemRepository.findFirstByUniqueKeyOrderByIdDesc(uniqueProblemKey)
                    .filter(p -> !"Done".equals(p.getStatus()));

            existing.ifPresent(problem -> {
                problem.setStatus("Done");
                problem.setClosedAt(LocalDateTime.now());

                GlobalProblem saved = problemRepository.save(problem);
                alertCache.removeAlert(saved.getId());
                sseService.sendAlert("ALERT_RESOLVED", saved);
            });
        }

        return ResponseEntity.ok("Nagios alert processed");
    }

    // TODO: Wyjebać to jakoś do ustawień żeby można było to konfigurować
    private int mapNagiosSeverityToLevel(String severityStr) {
        return switch (severityStr.toUpperCase()) {
            case "CRITICAL", "DOWN" -> 5;
            case "WARNING" -> 3;
            case "UNKNOWN", "UNREACHABLE" -> 2;
            case "OK", "UP" -> 0;
            default -> 1;
        };
    }
}