package pl.pjatk.alertwip.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pjatk.alertwip.dto.ActionRequestDTO;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.model.ProblemAction;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.repository.ProblemActionRepository;
import pl.pjatk.alertwip.service.ActiveAlertCache;
import pl.pjatk.alertwip.service.AlertActionService;
import pl.pjatk.alertwip.service.AlertHistoryService;
import pl.pjatk.alertwip.service.SseNotifService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private static final Logger log = LoggerFactory.getLogger(AlertController.class);

    private final GlobalProblemRepository problemRepository;
    private final SseNotifService sseService;
    private final AlertActionService alertActionService;
    private final ActiveAlertCache alertCache;
    private final ProblemActionRepository actionRepository;
    private final AlertHistoryService alertHistoryService;

    public AlertController(GlobalProblemRepository problemRepository,
                           SseNotifService sseService,
                           AlertActionService alertActionService,
                           ActiveAlertCache alertCache,
                           ProblemActionRepository actionRepository,
                           AlertHistoryService alertHistoryService) {
        this.problemRepository = problemRepository;
        this.sseService = sseService;
        this.alertActionService = alertActionService;
        this.alertCache = alertCache;
        this.actionRepository = actionRepository;
        this.alertHistoryService = alertHistoryService;
    }

    // usuwa prefix "GROUP_"
    private List<String> extractUserGroups(Authentication authentication) {
        if (authentication == null) return List.of();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("GROUP_"))
                .map(groupAuth -> groupAuth.substring(6))
                .collect(Collectors.toList());
    }

    // Pobieranie otwartych alertów
    @GetMapping("/active")
    public List<GlobalProblem> getActiveAlerts(Authentication authentication) {
        List<String> userGroups = extractUserGroups(authentication);
        log.info("Użytkownik {} prosi o aktywne alerty. Rozpoznane grupy: {}", authentication.getName(), userGroups);
        return alertCache.getActiveAlertsForGroups(userGroups);
    }

    // Subskrypcja SSE
    @GetMapping("/stream")
    public SseEmitter streamAlerts(Authentication authentication) {
        List<String> userGroups = extractUserGroups(authentication);
        log.info("Nowe połączenie SSE dla użytkownika {}. Grupy: {}", authentication.getName(), userGroups);
        return sseService.subscribe(userGroups);
    }

    @GetMapping("/history")
    public ResponseEntity<org.springframework.data.domain.Page<GlobalProblem>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortKey,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @ModelAttribute pl.pjatk.alertwip.dto.AlertHistoryFiltersDTO filters
    ) {

        log.info("Otrzymano zapytanie do historii z filtrami: {}", filters);
        log.info("Paginacja i sortowanie -> strona: {}, rozmiar: {}, sortKey: {}, sortOrder: {}", page, pageSize, sortKey, sortOrder);

        org.springframework.data.domain.Page<GlobalProblem> historyPage =
                alertHistoryService.getAlertHistory(page, pageSize, sortKey, sortOrder, filters);

        return ResponseEntity.ok(historyPage);
    }

    @PostMapping("/{id}/ack")
    public ResponseEntity<?> performAction(
            @PathVariable Long id,
            @RequestBody String rawBody) {
        System.out.println("====== DEBUG: SUROWY JSON Z SIECI ======");
        System.out.println(rawBody);
        System.out.println("========================================");

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            pl.pjatk.alertwip.dto.ActionRequestDTO request = mapper.readValue(rawBody, pl.pjatk.alertwip.dto.ActionRequestDTO.class);

            pl.pjatk.alertwip.model.ProblemAction action = alertActionService.processAction(id, request);
            return ResponseEntity.ok(action);

        } catch (Exception e) {
            System.err.println("Błąd parsowania: " + e.getMessage());
            return ResponseEntity.badRequest().body("Niepoprawny format danych");
        }
    }

    @GetMapping("/{id}/actions")
    public ResponseEntity<List<ProblemAction>> getAlertActions(@PathVariable Long id) {
        List<ProblemAction> history = actionRepository.findAllByProblemIdOrderByCreatedAtDesc(id);
        System.out.println(history.toString());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/origins")
    public ResponseEntity<List<String>> getAvailableOrigins() {
        return ResponseEntity.ok(alertHistoryService.getAllOriginTypes());
    }
}