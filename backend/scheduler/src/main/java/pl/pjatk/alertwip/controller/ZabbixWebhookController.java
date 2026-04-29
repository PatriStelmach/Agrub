package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.service.AlertRoutingService;
import pl.pjatk.alertwip.service.SseNotifService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks/zabbix")
public class ZabbixWebhookController {

    private final GlobalProblemRepository problemRepository;
    private final SseNotifService sseService;
    private final AlertRoutingService routingService;

    public ZabbixWebhookController(GlobalProblemRepository problemRepository,
                                   SseNotifService sseService,
                                   AlertRoutingService routingService) {
        this.problemRepository = problemRepository;
        this.sseService = sseService;
        this.routingService = routingService;
    }

    @PostMapping("/alert")
    public ResponseEntity<String> handleZabbixAlert(@RequestBody Map<String, Object> payload) {
        String eventStatus = (String) payload.getOrDefault("status", "PROBLEM");
        String eventName = (String) payload.getOrDefault("name", "Nieznany błąd");
        String host = (String) payload.getOrDefault("host", "Nieznany host");

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

                // --- MAGIA: Procesowanie widoczności przed zapisem! ---
                routingService.processVisibility(problem);

                GlobalProblem saved = problemRepository.save(problem);
                sseService.sendAlert("NEW_ALERT", saved);
            }

        } else if (eventStatus.equalsIgnoreCase("OK") || eventStatus.equalsIgnoreCase("RESOLVED")) {
            Optional<GlobalProblem> existing = problemRepository.findFirstByUniqueKeyOrderByIdDesc(uniqueProblemKey)
                    .filter(p -> !"Done".equals(p.getStatus()));

            existing.ifPresent(problem -> {
                problem.setStatus("Done");
                problem.setClosedAt(LocalDateTime.now());
                problemRepository.save(problem);

                sseService.sendAlert("ALERT_RESOLVED", problem);
            });
        }

        return ResponseEntity.ok("Alert processed");
    }
}