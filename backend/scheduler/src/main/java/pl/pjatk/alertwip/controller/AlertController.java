package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pjatk.alertwip.dto.ActionRequestDTO;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.model.ProblemAction;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.repository.ProblemActionRepository;
import pl.pjatk.alertwip.service.ActiveAlertCache;
import pl.pjatk.alertwip.service.AlertActionService;
import pl.pjatk.alertwip.service.SseNotifService;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final GlobalProblemRepository problemRepository;
    private final SseNotifService sseService;
    private final AlertActionService alertActionService;
    private final ActiveAlertCache alertCache;
    private final ProblemActionRepository actionRepository;

    public AlertController(GlobalProblemRepository problemRepository,
                           SseNotifService sseService,
                           AlertActionService alertActionService,
                           ActiveAlertCache alertCache,
                           ProblemActionRepository actionRepository) {
        this.problemRepository = problemRepository;
        this.sseService = sseService;
        this.alertActionService = alertActionService;
        this.alertCache = alertCache;
        this.actionRepository = actionRepository;
    }

    // Pobieranie otwartych alertów (Filtrowane po uprawnieniach usera z URL)
    @GetMapping("/active")
    public List<GlobalProblem> getActiveAlerts(@RequestParam(defaultValue = "ADMIN") List<String> groups) {
        // Odpytujemy naszą strukturę w pamięci!
        return alertCache.getActiveAlertsForGroups(groups);
    }

    // Subskrypcja SSE (Przekazujemy grupy usera z URL)
    @GetMapping("/stream")
    public SseEmitter streamAlerts(@RequestParam(defaultValue = "ADMIN") List<String> groups) {
        return sseService.subscribe(groups);
    }

    @PostMapping("/{id}/ack")
    public ResponseEntity<?> performAction(
            @PathVariable Long id,
            @RequestBody String rawBody) { // Odbieramy surowy tekst (String)

        System.out.println("====== DEBUG: SUROWY JSON Z SIECI ======");
        System.out.println(rawBody);
        System.out.println("========================================");

        try {
            // Ręcznie parsujemy JSONa na Twoje DTO, żeby sprawdzić co się przypisze
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            // Konfiguracja, żeby nie wywalało błędu przy nieznanych polach
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            pl.pjatk.alertwip.dto.ActionRequestDTO request = mapper.readValue(rawBody, pl.pjatk.alertwip.dto.ActionRequestDTO.class);

            System.out.println("====== DEBUG: PO MAPOWANIU NA DTO ======");
            System.out.println("Author: " + request.author());
            System.out.println("Acknowledge (zmienna acknowledge): " + request.acknowledge());
            System.out.println("New Severity: " + request.newSeverity());
            System.out.println("Message: " + request.message());
            System.out.println("==========================================");

            // Wywołanie serwisu
            pl.pjatk.alertwip.model.ProblemAction action = alertActionService.processAction(id, request);
            return ResponseEntity.ok(action);

        } catch (Exception e) {
            System.err.println("Błąd parsowania: " + e.getMessage());
            return ResponseEntity.badRequest().body("Niepoprawny format danych");
        }
    }
    @GetMapping("/{id}/actions")
    public ResponseEntity<List<ProblemAction>> getAlertActions(@PathVariable Long id) {
        // Zwracamy całą historię od najstarszego do najnowszego
        List<ProblemAction> history = actionRepository.findAllByProblemIdOrderByCreatedAtAsc(id);
        return ResponseEntity.ok(history);
    }
}