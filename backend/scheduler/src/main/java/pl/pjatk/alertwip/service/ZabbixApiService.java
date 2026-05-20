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

        try {
            //aktywne alerty
            Map<String, Object> problemRequest = Map.of(
                    "jsonrpc", "2.0",
                    "method", "problem.get",
                    "auth", apiToken,
                    "params", Map.of(
                            "output", List.of("eventid"),
                            "recent", false
                    ),
                    "id", 1
            );

            Map<String, Object> problemResponse = restClient.post()
                    .uri(apiUrl).body(problemRequest).retrieve().body(Map.class);

            if (problemResponse == null || !problemResponse.containsKey("result")) {
                return List.of();
            }

            List<Map<String, Object>> activeProblems = (List<Map<String, Object>>) problemResponse.get("result");
            if (activeProblems.isEmpty()) {
                return List.of();
            }

            List<String> eventIds = activeProblems.stream()
                    .map(p -> p.get("eventid").toString())
                    .toList();

            //szczegóły + nazwy hostów

            Map<String, Object> eventRequest = Map.of(
                    "jsonrpc", "2.0",
                    "method", "event.get",
                    "auth", apiToken,
                    "params", Map.of(
                            "eventids", eventIds,
                            "output", List.of("eventid", "name", "severity"),
                            "selectHosts", List.of("name")
                    ),
                    "id", 2
            );

            Map<String, Object> eventResponse = restClient.post()
                    .uri(apiUrl).body(eventRequest).retrieve().body(Map.class);

            if (eventResponse != null && eventResponse.containsKey("result")) {
                List<Map<String, Object>> finalResult = (List<Map<String, Object>>) eventResponse.get("result");

                System.out.println("=== [DIAGNOSTYKA] DANE Z ZAPYTANIA (problem + event) ===");
                System.out.println(finalResult);
                System.out.println("========================================================");

                return finalResult;
            }

            return List.of();

        } catch (Exception e) {
            throw new RuntimeException("Błąd pobierania problemów (Dwustopniowe API): " + e.getMessage());
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