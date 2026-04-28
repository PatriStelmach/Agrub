package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.*;
import pl.pjatk.alertwip.dto.ActionRequestDTO;
import pl.pjatk.alertwip.repository.GlobalProblemRepository; // Zmień na nazwę swojego repozytorium alertów
import pl.pjatk.alertwip.repository.ProblemActionRepository;
import pl.pjatk.alertwip.service.adapter.AlertSourceAdapter;

import java.util.List;

@Service
public class AlertActionService {

    private final GlobalProblemRepository problemRepository;
    private final ProblemActionRepository actionRepository;
    private final List<AlertSourceAdapter> adapters; // Spring automatycznie wstrzyknie tu ZabbixAdapter i DummyAdapter!
    private final SseNotifService sseService;

    public AlertActionService(GlobalProblemRepository problemRepository,
                              ProblemActionRepository actionRepository,
                              List<AlertSourceAdapter> adapters,
                              SseNotifService sseService) {
        this.problemRepository = problemRepository;
        this.actionRepository = actionRepository;
        this.adapters = adapters;
        this.sseService = sseService;
    }

    public ProblemAction processAction(Long problemId, ActionRequestDTO request) {
        // 1. Znajdź problem w bazie
        GlobalProblem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono problemu o ID: " + problemId));

        // 2. Utwórz wpis w historii (ProblemAction)
        ProblemAction action = new ProblemAction();
        action.setProblem(problem);

        // ZMIENIONE: Usunięto przedrostki "get"
        action.setAuthor(request.author());
        action.setActionType(request.actionType());
        action.setMessage(request.message());

        action.setSyncStatus(SyncStatus.PENDING);

        // Aktualizacja stanu samego alertu
        if (request.actionType() == ActionType.ACK) {
            problem.setAcknowledged(true);
        } else if (request.actionType() == ActionType.UNACK) {
            problem.setAcknowledged(false);
        }

        // Zapisujemy najpierw lokalnie
        problemRepository.save(problem);
        ProblemAction savedAction = actionRepository.save(action);

        // 3. Natychmiastowe powiadomienie Vue.js przez SSE (żeby reszta zespołu widziała zmianę)
        sseService.sendAlert("ALERT_UPDATED", problem);

        // 4. Wyszukanie odpowiedniego adaptera (Zabbix, Nagios, Dummy) i wysłanie do źródła
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