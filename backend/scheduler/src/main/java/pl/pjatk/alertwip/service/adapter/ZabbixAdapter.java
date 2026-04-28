package pl.pjatk.alertwip.service.adapter;

import org.springframework.stereotype.Component;
import pl.pjatk.alertwip.dto.ActionRequestDTO;
import pl.pjatk.alertwip.model.GlobalProblem;
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
    public boolean sendAction(ActionRequestDTO request, GlobalProblem problem) {
        if (problem.getExternalEventId() == null) {
            System.err.println("[ZabbixAdapter] BŁĄD: Brak externalEventId dla alertu!");
            return false;
        }

        int actionBitmask = 0;
        Map<String, Object> params = new HashMap<>();
        params.put("eventids", problem.getExternalEventId());

        // 1. Sprawdzamy ACK/UNACK (tylko jeśli podano)
        if (request.acknowledge() != null) {
            if (request.acknowledge()) {
                actionBitmask += 2;  // Zabbix: Acknowledge event
            } else {
                actionBitmask += 16; // Zabbix: Unacknowledge event
            }
        }

        // 2. Sprawdzamy zmianę Severity (tylko jeśli podano)
        if (request.newSeverity() != null) {
            actionBitmask += 8;  // Zabbix: Change severity
            params.put("severity", String.valueOf(request.newSeverity()));
        }

        // 3. Sprawdzamy dodanie wiadomości (tylko jeśli podano)
        if (request.message() != null && !request.message().isBlank()) {
            actionBitmask += 4;  // Zabbix: Add message
            String author = (request.author() != null && !request.author().isBlank()) ? request.author() : "System";
            params.put("message", author + ": " + request.message());
        }

        // Jeśli bitmaska to 0, frontend przysłał pustego JSON-a - nie pytamy API
        if (actionBitmask == 0) {
            System.out.println("[ZabbixAdapter] Brak akcji do wysłania do API dla EventID: " + problem.getExternalEventId());
            return true;
        }

        params.put("action", actionBitmask);

        try {
            var response = zabbixApiService.callZabbixApi("event.acknowledge", params);
            System.out.println("[ZabbixAdapter] Pomyślnie wysłano akcję (Maska: " + actionBitmask + ") do Zabbixa dla EventID: " + problem.getExternalEventId());
            return true;

        } catch (Exception e) {
            System.err.println("[ZabbixAdapter] Błąd komunikacji z Zabbixem: " + e.getMessage());
            return false;
        }
    }
}