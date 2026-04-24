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

        // WYCIĄGAMY SEVERITY Z JSONA
        int severity = 0;
        Object sevObj = payload.get("severity");
        if (sevObj != null) {
            try {
                severity = Integer.parseInt(sevObj.toString());
            } catch (NumberFormatException ignored) {}
        }

        // Używamy nowego UniqueKey (odpowiednik starego taskName)
        String uniqueProblemKey = "[ZABBIX] " + host + " - " + eventName;

        if (eventStatus.equalsIgnoreCase("PROBLEM")) {
            // Szukamy, czy ten problem już istnieje i NIE JEST zamknięty
            Optional<GlobalProblem> existing = problemRepository.findByUniqueKey(uniqueProblemKey)
                    .filter(p -> !"Done".equals(p.getStatus()));

            if (existing.isEmpty()) {
                GlobalProblem problem = new GlobalProblem();

                // Mapowanie na nowy model (zgodnie ze standardem ITSM)
                problem.setUniqueKey(uniqueProblemKey);
                problem.setSubject(eventName);
                problem.setSource(host);
                problem.setContent("Alert odebrany z Zabbixa z hosta: " + host);
                problem.setStatus("Sent"); // Oznaczamy jako nowy
                problem.setSeverity(severity);
                problem.setCreatedAt(LocalDateTime.now());

                GlobalProblem saved = problemRepository.save(problem);
                sseService.sendAlert("NEW_ALERT", saved);
                System.out.println("[WEBHOOK] Otrzymano nowy alert z Zabbixa: " + uniqueProblemKey);
            }

        } else if (eventStatus.equalsIgnoreCase("OK") || eventStatus.equalsIgnoreCase("RESOLVED")) {
            // Skoro jest OK, szukamy otwartego problemu i oznaczamy jako DONE
            Optional<GlobalProblem> existing = problemRepository.findByUniqueKey(uniqueProblemKey)
                    .filter(p -> !"Done".equals(p.getStatus()));

            existing.ifPresent(problem -> {
                // MIĘKKIE USUWANIE (Soft Delete) - Archiwizujemy problem
                problem.setStatus("Done");
                problem.setClosedAt(LocalDateTime.now());

                problemRepository.save(problem);

                // INFORMUJEMY VUE O NAPRAWIE!
                sseService.sendAlert("ALERT_RESOLVED", problem);
                System.out.println("[WEBHOOK] Alert rozwiązany: " + uniqueProblemKey);
            });
        }

        return ResponseEntity.ok("Alert processed");
    }
}