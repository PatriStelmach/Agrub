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
        String apiToken = settingService.getValue("zabbix_api_token_SECRET", "");

        if (apiUrl.isBlank() || apiToken.isBlank()) {
            throw new IllegalStateException("Konfiguracja Zabbixa jest niekompletna. Skonfiguruj API Token w panelu.");
        }

        Map<String, Object> request = Map.of(
                "jsonrpc", "2.0",
                "method", "problem.get",
                "auth", apiToken,
                "params", Map.of(
                        "output", List.of("eventid", "name", "severity"),
                        "selectHosts", List.of("host"),
                        "recent", false
                ),
                "id", 1
        );

        try {
            Map<String, Object> response = restClient.post()
                    .uri(apiUrl)
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("result")) {
                return (List<Map<String, Object>>) response.get("result");
            }
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Błąd pobierania problemów: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> callZabbixApi(String method, Map<String, Object> params) {
        String apiUrl = settingService.getValue("zabbix_url", "");
        String apiToken = settingService.getValue("zabbix_api_token_SECRET", "");

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