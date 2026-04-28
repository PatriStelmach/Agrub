package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pjatk.alertwip.dto.ActionRequestDTO;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.model.ProblemAction;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.service.AlertActionService;
import pl.pjatk.alertwip.service.SseNotifService;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final GlobalProblemRepository problemRepository;
    private final SseNotifService sseService;
    private final AlertActionService alertActionService;

    public AlertController(GlobalProblemRepository problemRepository,
                           SseNotifService sseService,
                           AlertActionService alertActionService) {
        this.problemRepository = problemRepository;
        this.sseService = sseService;
        this.alertActionService = alertActionService;
    }

    // Pobieranie otwartych alertów (Filtrowane po uprawnieniach usera z URL)
    @GetMapping("/active")
    public List<GlobalProblem> getActiveAlerts(@RequestParam(defaultValue = "ADMIN") List<String> groups) {
        return problemRepository.findAll().stream()
                .filter(p -> !"Done".equals(p.getStatus()))
                // Filtrujemy bazę - zwracamy tylko te, które mają wspólną grupę
                .filter(p -> p.getTechnicianGroups().stream().anyMatch(groups::contains))
                .toList();
    }

    // Subskrypcja SSE (Przekazujemy grupy usera z URL)
    @GetMapping("/stream")
    public SseEmitter streamAlerts(@RequestParam(defaultValue = "ADMIN") List<String> groups) {
        return sseService.subscribe(groups);
    }

    @PostMapping("/{id}/action")
    public ResponseEntity<ProblemAction> performAction(
            @PathVariable Long id,
            @RequestBody ActionRequestDTO request) {

        // Wywołujemy nasz "Mózg Operacji"
        ProblemAction action = alertActionService.processAction(id, request);

        return ResponseEntity.ok(action);
    }
}