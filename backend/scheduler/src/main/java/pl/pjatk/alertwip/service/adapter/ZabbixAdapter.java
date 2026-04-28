package pl.pjatk.alertwip.service.adapter;

import org.springframework.stereotype.Component;
import pl.pjatk.alertwip.model.ActionType;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.model.ProblemAction;
import pl.pjatk.alertwip.service.ZabbixApiService; // Użyj swojej nazwy serwisu

import java.util.HashMap;
import java.util.Map;

@Component
public class ZabbixAdapter implements AlertSourceAdapter {

    // Wstrzykujemy serwis, który ma już skonfigurowany RestTemplate/WebClient i pobiera klucz z ustawień
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
        }

        if (action.getMessage() != null && !action.getMessage().isBlank()) {
            actionBitmask += 4;  // Zabbix: Add message
        }

        // 2. Budowanie parametrów dla API Zabbixa
        Map<String, Object> params = new HashMap<>();
        params.put("eventids", problem.getExternalEventId());
        params.put("action", actionBitmask);

        if (action.getMessage() != null && !action.getMessage().isBlank()) {
            // Formatujemy wiadomość: "admin: Rozpoczynam diagnostykę"
            params.put("message", action.getAuthor() + ": " + action.getMessage());
        }

        // 3. Właściwe wywołanie API
        try {
            // Zakładam, że Twój ZabbixApiService ma metodę typu callZabbixApi(method, params)
            // która sama dokleja token z bazy i ogarnia JSON-RPC.
            // Jeśli metoda nazywa się inaczej, po prostu ją tu podmień:
            var response = zabbixApiService.callZabbixApi("event.acknowledge", params);

            // Opcjonalnie: możesz tu sprawdzić, czy odpowiedź nie zawiera błędu JSON-RPC ("error")
            System.out.println("[ZabbixAdapter] Pomyślnie wysłano akcję do Zabbixa dla EventID: " + problem.getExternalEventId());
            return true;

        } catch (Exception e) {
            System.err.println("[ZabbixAdapter] Błąd komunikacji z Zabbixem: " + e.getMessage());
            return false; // Zwracamy false, żeby system wiedział, że ma ustawić SyncStatus.FAILED
        }
    }
}