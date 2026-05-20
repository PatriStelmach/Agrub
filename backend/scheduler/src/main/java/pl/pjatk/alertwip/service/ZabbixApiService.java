package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ZabbixApiService {

    private final RestClient restClient;
    private final SystemSettingService settingService;

    public ZabbixApiService(RestClient restClient, SystemSettingService settingService) {
        this.restClient = restClient;
        this.settingService = settingService;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getActiveProblems() {
        String apiUrl = settingService.getValue("zabbix_url", "");
        String apiToken = settingService.getValue("zabbix_password_SECRET", "");

        if (apiUrl.isBlank() || apiToken.isBlank()) {
            throw new IllegalStateException("Konfiguracja Zabbixa jest niekompletna. Skonfiguruj API Token w panelu.");
        }

        Map<String, Object> request = Map.of(
                "jsonrpc", "2.0",
                "method", "trigger.get",
                "auth", apiToken,
                "params", Map.of(
                        "output", List.of("triggerid", "description", "priority"),
                        "selectHosts", List.of("name"), // nazwa hosta
                        "selectLastEvent", List.of("eventid"), // do pobrania eventId
                        "monitored", true,
                        "active", true,
                        "skipDependent", true,
                        "only_true", true
                ),
                "id", 1
        );

        try {
            Map<String, Object> response = restClient.post()
                    .uri(apiUrl)
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            // ========== LOGOWANIE ===========
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
                System.out.println("\n=== [DIAGNOSTYKA] SUROWA ODPOWIEDŹ ZABBIX API (trigger.get) ===");
                System.out.println(mapper.writeValueAsString(response));
                System.out.println("===============================================================\n");
            } catch (Exception printEx) {
                System.out.println("[DIAGNOSTYKA] Nie udało się sformatować JSON-a. Surowa mapa: " + response);
            }

            //======================
            
            if (response != null && response.containsKey("result")) {
                return (List<Map<String, Object>>) response.get("result");
            }
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Błąd pobierania problemów (trigger.get): " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> callZabbixApi(String method, Map<String, Object> params) {
        String apiUrl = settingService.getValue("zabbix_url", "");
        String apiToken = settingService.getValue("zabbix_password_SECRET", "");

        if (apiUrl.isBlank() || apiToken.isBlank()) {
            throw new IllegalStateException("Konfiguracja Zabbixa jest niekompletna. Skonfiguruj API Token w panelu.");
        }

        Map<String, Object> request = Map.of(
                "jsonrpc", "2.0",
                "method", method,
                "auth", apiToken,
                "params", params,
                "id", System.currentTimeMillis() // unikalne ID zapytania
        );

        try {
            return restClient.post()
                    .uri(apiUrl)
                    .body(request)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Błąd komunikacji z Zabbix API (" + method + "): " + e.getMessage());
        }
    }

}