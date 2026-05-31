package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.dto.ProblemActionDTO;
import pl.pjatk.alertwip.model.*;
import pl.pjatk.alertwip.dto.ActionRequestDTO;
import pl.pjatk.alertwip.dto.AlertUpdateEventDTO;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.repository.ProblemActionRepository;
import pl.pjatk.alertwip.service.adapter.AlertSourceAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        ProblemAction action = new ProblemAction();
        action.setProblem(problem);
        action.setAuthor(request.author() != null && !request.author().isBlank() ? request.author() : "System");  // Jesli author to null, zmieniamy na System
        action.setMessage(request.message());
        action.setSyncStatus(SyncStatus.PENDING);

        //zmieniamy acka jesli w request jest inny
        if (request.acknowledge() != null && problem.isAcknowledged() != request.acknowledge()){
            problem.setAcknowledged(request.acknowledge());

            //czas pierwszego acka dla audytu
            if(request.acknowledge() && problem.getAcknowledgedAt() == null){
                problem.setAcknowledgedAt(LocalDateTime.now());

                //jeśli to wazuh to zamykamy od razy
                if(problem.getOriginType().equals("WAZUH")){
                    problem.setStatus("Done");
                    problem.setClosedAt(LocalDateTime.now());
                }
            }

            action.setAckUpdate(request.acknowledge());
            stateChanged = true;
        }

        //zmiana severity
        if (request.newSeverity() != null && !request.newSeverity().equals(problem.getSeverity())){
            action.setPreviousSeverity(problem.getSeverity());
            action.setNewSeverity(request.newSeverity());

            problem.setSeverity(request.newSeverity());
            problem.setSeverityLocked(true);
            stateChanged = true;
        }

        boolean isNowClosed = problem.getStatus().equals("Done");

        // Jeśli zmienił się stan głównego alertu, nadpisujemy go w bazie i aktualizujemy Cache
        if (stateChanged) {
            problem = problemRepository.save(problem);

            if(isNowClosed) {
                alertCache.removeAlert(problem.getId());
                sseService.sendAlert("ALERT_RESOLVED", problem);
            } else {
                alertCache.updateAlert(problem);
                // tu NIE ma być sse
            }
        }

        ProblemAction savedAction = actionRepository.save(action);
        problem.getActions().add(savedAction);

        // 4. Budowanie i wysyłka lekkiego DTO z "Diffem" na frontend
        if (!isNowClosed) {
            AlertUpdateEventDTO eventPayload = new AlertUpdateEventDTO(
                    savedAction.getId(),
                    problem.getId(),
                    savedAction.getAuthor(),
                    action.getAckUpdate(), // null jeśli nie zmieniono w żądaniu
                    action.getMessage(),     // null jeśli brak wiadomości
                    savedAction.getCreatedAt(),
                    action.getPreviousSeverity(),
                    action.getNewSeverity()  // null jeśli nie zmieniono w żądaniu
            );

            // Wysyłamy wyłącznie zmiany (eventPayload).
            sseService.sendAlertUpdate("ALERT_UPDATE_ONLY", eventPayload, problem);
        }

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

    // pobranie wszystkich akcji dla usera
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ProblemAction> getFilteredActionsByAuthor(
            String author,
            pl.pjatk.alertwip.dto.AlertHistoryFiltersDTO filters,
            org.springframework.data.domain.Pageable pageable) {

        // Budowanie specyfikacji filtrowania
        var spec = pl.pjatk.alertwip.repository.specification.ProblemActionSpecifications
                .buildActionFilter(author, filters);

        return actionRepository.findAll(spec, pageable);
    }
}