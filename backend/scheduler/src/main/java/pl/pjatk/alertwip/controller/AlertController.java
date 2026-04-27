package pl.pjatk.alertwip.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.service.SseNotifService;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final GlobalProblemRepository problemRepository;
    private final SseNotifService sseService;

    public AlertController(GlobalProblemRepository problemRepository, SseNotifService sseService) {
        this.problemRepository = problemRepository;
        this.sseService = sseService;
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
}