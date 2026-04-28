package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.*;
import pl.pjatk.alertwip.dto.ActionRequestDTO;
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

    public ProblemAction processAction(Long problemId, ActionRequestDTO request) {
        // 1. Znajdź problem w bazie
        GlobalProblem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono problemu o ID: " + problemId));

        // 2. Utwórz wpis w historii
        ProblemAction action = new ProblemAction();
        action.setProblem(problem);

        action.setAuthor(request.author());
        action.setActionType(request.actionType());
        action.setMessage(request.message());

        action.setSyncStatus(SyncStatus.PENDING);

        // Aktualizacja stanu samego alertu
        if (request.actionType() == ActionType.ACK) {
            problem.setAcknowledged(true);
        } else if (request.actionType() == ActionType.UNACK) {
            problem.setAcknowledged(false);
        } else if (request.actionType() == ActionType.SEVERITY_CHANGE && request.newSeverity() != null) {
            problem.setSeverity(request.newSeverity());
        }
        problemRepository.save(problem);
        ProblemAction savedAction = actionRepository.save(action);
        problem.getActions().add(savedAction);
        alertCache.updateAlert(problem);

        // 3. Natychmiastowe powiadomienie
        sseService.sendAlert("ALERT_UPDATED", problem);
        sseService.sendAlert("NEW_ACTION", savedAction);

        // 4. Wyszukanie odpowiedniego adaptera
        AlertSourceAdapter matchedAdapter = adapters.stream()
                .filter(a -> a.supports(problem.getOriginType()))
                .findFirst()
                .orElse(null);

        if (matchedAdapter != null) {
            boolean success = matchedAdapter.sendAction(savedAction, problem);
            savedAction.setSyncStatus(success ? SyncStatus.SYNCED : SyncStatus.FAILED);
            actionRepository.save(savedAction); // Aktualizacja statusu synchronizacji
        } else {
            System.err.println("Brak adaptera obsługującego typ źródła: " + problem.getOriginType());
            savedAction.setSyncStatus(SyncStatus.FAILED);
            actionRepository.save(savedAction);
        }

        return savedAction;
    }
}