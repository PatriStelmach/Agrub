package pl.pjatk.alertwip.service.adapter;

import org.springframework.stereotype.Component;
import pl.pjatk.alertwip.model.ActionType;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.model.ProblemAction;
import pl.pjatk.alertwip.service.ZabbixApiService;

import java.util.HashMap;
import java.util.Map;

@Component
public class ZabbixAdapter implements AlertSourceAdapter {
    private final ZabbixApiService zabbixApiService;

    public ZabbixAdapter(ZabbixApiService zabbixApiService) {
        this.zabbixApiService = zabbixApiService;
    }

    @Override
    public boolean supports(String originType) {
        return "ZABBIX".equalsIgnoreCase(originType);
    }

    @Override
    public boolean sendAction(ProblemAction action, GlobalProblem problem) {
        if (problem.getExternalEventId() == null) {
            System.err.println("[ZabbixAdapter] BŁĄD: Brak externalEventId dla alertu!");
            return false;
        }

        // 1. Obliczanie Bitmaski Akcji
        int actionBitmask = 0;

        if (action.getActionType() == ActionType.ACK) {
            actionBitmask += 2;  // Zabbix: Acknowledge event
        } else if (action.getActionType() == ActionType.UNACK) {
            actionBitmask += 16; // Zabbix: Unacknowledge event
        } else if (action.getActionType() == ActionType.SEVERITY_CHANGE) {
            actionBitmask += 8;  // Zabbix: Zmiana severity
        }

        // Komentarz może być dla każdej
        if (action.getMessage() != null && !action.getMessage().isBlank()) {
            actionBitmask += 4;  // Zabbix: Add message
        }

        // 2. Budowanie parametrów dla API Zabbixa
        Map<String, Object> params = new HashMap<>();
        params.put("eventids", problem.getExternalEventId());
        params.put("action", actionBitmask);

        if (action.getMessage() != null && !action.getMessage().isBlank()) {
            params.put("message", action.getAuthor() + ": " + action.getMessage());
        }

        if (action.getActionType() == ActionType.SEVERITY_CHANGE) {
            params.put("severity", String.valueOf(problem.getSeverity()));
        }

        // 3. Właściwe wywołanie API
        try {
            var response = zabbixApiService.callZabbixApi("event.acknowledge", params);
            System.out.println("[ZabbixAdapter] Pomyślnie wysłano akcję do Zabbixa dla EventID: " + problem.getExternalEventId());
            return true;

        } catch (Exception e) {
            System.err.println("[ZabbixAdapter] Błąd komunikacji z Zabbixem: " + e.getMessage());
            return false; // Zwracamy false, żeby system wiedział, że ma ustawić SyncStatus.FAILED
        }
    }
}