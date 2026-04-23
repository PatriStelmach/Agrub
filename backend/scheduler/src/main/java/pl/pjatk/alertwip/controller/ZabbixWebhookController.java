package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.service.SseNotifService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks/zabbix")
public class ZabbixWebhookController {

    private final GlobalProblemRepository problemRepository;
    private final SseNotifService sseService;

    // Wstrzykujemy zależności
    public ZabbixWebhookController(GlobalProblemRepository problemRepository, SseNotifService sseService) {
        this.problemRepository = problemRepository;
        this.sseService = sseService;
    }

    @PostMapping("/alert")
    public ResponseEntity<String> handleZabbixAlert(@RequestBody Map<String, Object> payload) {
        String eventStatus = (String) payload.getOrDefault("status", "PROBLEM");
        String eventName = (String) payload.getOrDefault("name", "Nieznany błąd");
        String host = (String) payload.getOrDefault("host", "Nieznany host");

        // WYCIĄGAMY SEVERITY Z JSONA (jeśli Zabbix tego nie prześle, domyślnie "0")
        int severity = 0;
        Object sevObj = payload.get("severity");
        if (sevObj != null) {
            try {
                severity = Integer.parseInt(sevObj.toString());
            } catch (NumberFormatException ignored) {}
        }

        String uniqueProblemName = "[ZABBIX] " + host + " - " + eventName;

        if (eventStatus.equalsIgnoreCase("PROBLEM")) {
            Optional<GlobalProblem> existing = problemRepository.findByTaskName(uniqueProblemName);

            if (existing.isEmpty()) {
                GlobalProblem problem = new GlobalProblem();
                problem.setTaskName(uniqueProblemName);
                problem.setLastErrorMessage("Alert odebrany z Zabbixa z hosta: " + host);

                // USTAWIAMY OSOBNE POLE SEVERITY
                problem.setSeverity(severity);

                problem.setOccurrenceTime(LocalDateTime.now());

                GlobalProblem saved = problemRepository.save(problem);
                sseService.sendAlert("NEW_ALERT", saved);
            }

        } else if (eventStatus.equalsIgnoreCase("OK") || eventStatus.equalsIgnoreCase("RESOLVED")) {
            // Skoro jest OK, szukamy problemu i go usuwamy (Recovery)
            Optional<GlobalProblem> existing = problemRepository.findByTaskName(uniqueProblemName);

            if (existing.isPresent()) {
                GlobalProblem problemToResolve = existing.get();
                problemRepository.delete(problemToResolve);

                // INFORMUJEMY VUE O NAPRAWIE!
                sseService.sendAlert("ALERT_RESOLVED", problemToResolve);
            }
        }

        return ResponseEntity.ok("Alert processed");
    }
}