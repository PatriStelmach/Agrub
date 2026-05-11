package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.*;
import pl.pjatk.alertwip.dto.ActionRequestDTO;
import pl.pjatk.alertwip.dto.AlertUpdateEventDTO;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.repository.ProblemActionRepository;
import pl.pjatk.alertwip.service.adapter.AlertSourceAdapter;

import java.util.List;

@Service
public class AlertActionService {

    private final GlobalProblemRepository problemRepository;
    private final ProblemActionRepository actionRepository;
    private final List<AlertSourceAdapter> adapters;
    private final SseNotifService sseService;
    private final ActiveAlertCache alertCache;

    public AlertActionService(GlobalProblemRepository problemRepository,
                              ProblemActionRepository actionRepository,
                              List<AlertSourceAdapter> adapters,
                              SseNotifService sseService,
                              ActiveAlertCache alertCache) {
        this.problemRepository = problemRepository;
        this.actionRepository = actionRepository;
        this.adapters = adapters;
        this.sseService = sseService;
        this.alertCache = alertCache;
    }

    @org.springframework.transaction.annotation.Transactional
    public ProblemAction processAction(Long problemId, ActionRequestDTO request) {
        // 1. Znajdź problem w bazie
        GlobalProblem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono problemu o ID: " + problemId));

        GlobalProblem cachedAlert = alertCache.getAlertById(problemId);
        boolean wasLocked = cachedAlert != null && cachedAlert.isSeverityLocked();
        problem.setSeverityLocked(wasLocked);

        boolean stateChanged = false;
        ActionType inferredActionType = ActionType.COMMENT; // Domyślny typ akcji dla historii

        // 2. Dynamiczna aktualizacja stanu alertu na podstawie przesłanych danych
        if (request.acknowledge() != null && problem.isAcknowledged() != request.acknowledge()) {
            problem.setAcknowledged(request.acknowledge());
            stateChanged = true;
            inferredActionType = request.acknowledge() ? ActionType.ACK : ActionType.UNACK;
        }

        if (request.newSeverity() != null && !request.newSeverity().equals(problem.getSeverity())) {
            problem.setSeverity(request.newSeverity());
            stateChanged = true;
            inferredActionType = ActionType.SEVERITY_CHANGE;

            problem.setSeverityLocked(true);
        }

        // Jeśli zmienił się stan głównego alertu, nadpisujemy go w bazie i aktualizujemy Cache
        if (stateChanged) {
            problem = problemRepository.save(problem);

            if (wasLocked || inferredActionType == ActionType.SEVERITY_CHANGE) {
                problem.setSeverityLocked(true);
            }

            alertCache.updateAlert(problem);
            // tu NIE ma być sse
        }

        // 3. Utworzenie wpisu w historii operacji (Audyt)
        ProblemAction action = new ProblemAction();
        action.setProblem(problem);
        action.setAuthor(request.author() != null && !request.author().isBlank() ? request.author() : "System");
        action.setMessage(request.message());
        action.setActionType(inferredActionType); // Zapisujemy wywnioskowany typ
        action.setSyncStatus(SyncStatus.PENDING);

        ProblemAction savedAction = actionRepository.save(action);
        problem.getActions().add(savedAction);

        // 4. Budowanie i wysyłka lekkiego DTO z "Diffem" na frontend
        AlertUpdateEventDTO eventPayload = new AlertUpdateEventDTO(
                savedAction.getId(),
                problem.getId(),
                savedAction.getAuthor(),
                request.acknowledge(), // null jeśli nie zmieniono w żądaniu
                request.message(),     // null jeśli brak wiadomości
                savedAction.getCreatedAt(),
                request.newSeverity()  // null jeśli nie zmieniono w żądaniu
        );

        // Wysyłamy wyłącznie zmiany (eventPayload).
        sseService.sendAlertUpdate("ALERT_UPDATE_ONLY", eventPayload, problem);

        // 5. Delegacja do odpowiedniego adaptera (Zabbix, Wazuh)
        String originType = problem.getOriginType();

        AlertSourceAdapter matchedAdapter = adapters.stream()
                .filter(a -> a.supports(originType))
                .findFirst()
                .orElse(null);

        if (matchedAdapter != null) {
            // Przekazujemy oryginalne DTO (request) oraz stan alertu
            boolean success = matchedAdapter.sendAction(request, problem);
            savedAction.setSyncStatus(success ? SyncStatus.SYNCED : SyncStatus.FAILED);
            actionRepository.save(savedAction);
        } else {
            System.err.println("Brak adaptera obsługującego typ źródła: " + problem.getOriginType());
            savedAction.setSyncStatus(SyncStatus.FAILED);
            actionRepository.save(savedAction);
        }

        return savedAction;
    }
}