package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ZabbixApiService {

    private final RestTemplate restTemplate;
    private final SystemSettingService settingService;

    public ZabbixApiService(RestTemplate restTemplate, SystemSettingService settingService) {
        this.restTemplate = restTemplate;
        this.settingService = settingService;
    }

    public List<Map<String, Object>> getActiveProblems() {
        // Zamiast user/password pobieramy tylko URL i gotowy Token API
        String apiUrl = settingService.getValue("zabbix_url", "");
        String apiToken = settingService.getValue("zabbix_api_token", "");

        if (apiUrl.isBlank() || apiToken.isBlank()) {
            throw new IllegalStateException("Konfiguracja Zabbixa jest niekompletna. Skonfiguruj API Token w panelu.");
        }

        // Budujemy od razu zapytanie docelowe, wstrzykując Token do pola "auth"
        Map<String, Object> request = Map.of(
                "jsonrpc", "2.0",
                "method", "problem.get",
                "auth", apiToken, // <-- O tutaj idzie nasz token!
                "params", Map.of(
                        "output", List.of("eventid", "name", "severity"),
                        "selectHosts", List.of("host"),
                        "recent", false
                ),
                "id", 1
        );

        try {
            Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);
            if (response != null && response.containsKey("result")) {
                return (List<Map<String, Object>>) response.get("result");
            }
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Błąd pobierania problemów: " + e.getMessage());
        }
    }
}