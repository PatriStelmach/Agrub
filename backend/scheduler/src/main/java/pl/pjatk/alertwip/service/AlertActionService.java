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
        }

        // Jeśli zmienił się stan głównego alertu, nadpisujemy go w bazie i powiadamiamy RAM/SSE
        if (stateChanged) {
            problem = problemRepository.save(problem);
            alertCache.updateAlert(problem);
            sseService.sendAlert("ALERT_UPDATE", problem);
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

        // Zawsze powiadamiamy frontend o nowym komentarzu/akcji w historii
        sseService.sendAlert("NEW_ACTION", savedAction);

        // 4. Delegacja do odpowiedniego adaptera (Zabbix, Wazuh)
        String originType = problem.getOriginType();

        AlertSourceAdapter matchedAdapter = adapters.stream()
                .filter(a -> a.supports(originType))
                .findFirst()
                .orElse(null);

        if (matchedAdapter != null) {
            // Przekazujemy oryginalne DTO (request) oraz stan alertu, dokładnie tak jak wymaga zaktualizowany interfejs
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