package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.service.ActiveAlertCache;
import pl.pjatk.alertwip.service.NagiosApiService;
import pl.pjatk.alertwip.service.SseNotifService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/nagios")
public class NagiosController {

    private final NagiosApiService nagiosApi;
    private final GlobalProblemRepository problemRepository;
    private final ActiveAlertCache alertCache;
    private final SseNotifService sseService;

    public NagiosController(NagiosApiService nagiosApi,
                            GlobalProblemRepository problemRepository,
                            ActiveAlertCache alertCache,
                            SseNotifService sseService) {
        this.nagiosApi = nagiosApi;
        this.problemRepository = problemRepository;
        this.alertCache = alertCache;
        this.sseService = sseService;
    }

    @GetMapping("/hosts")
    public ResponseEntity<String> getHosts() {
        try {
            return ResponseEntity.ok(nagiosApi.fetchHosts());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/problems")
    public ResponseEntity<List<GlobalProblem>> getAllNagiosProblems() {
        return ResponseEntity.ok(problemRepository.findByOriginType("NAGIOS"));
    }

    @PatchMapping("/problems/{id}/close")
    public ResponseEntity<GlobalProblem> closeProblem(@PathVariable Long id) {
        return problemRepository.findById(id).map(problem -> {
            problem.setStatus("Done");
            problem.setClosedAt(LocalDateTime.now());
            problem.setAcknowledged(true);

            GlobalProblem saved = problemRepository.save(problem);

            alertCache.removeAlert(id);
            sseService.sendAlert("ALERT_RESOLVED", saved);

            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }
}