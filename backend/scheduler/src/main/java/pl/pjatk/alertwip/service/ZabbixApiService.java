package pl.pjatk.alertwip.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class ZabbixApiService {

    @Value("${zabbix.api.url}")
    private String apiUrl;

    @Value("${zabbix.api.user}")
    private String username;

    @Value("${zabbix.api.password}")
    private String password;

    private String authToken;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Logowanie do Zabbix API w celu uzyskania tokena sesji.
     */
    public String login() {
        Map<String, Object> request = Map.of(
                "jsonrpc", "2.0",
                "method", "user.login",
                "params", Map.of("user", username, "password", password),
                "id", 1,
                "auth", null
        );
        Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);
        if (response != null && response.containsKey("result")) {
            this.authToken = (String) response.get("result");
        }
        return authToken;
    }

    /**
     * Pobieranie aktualnie trwających problemów.
     */
    public List<Map<String, Object>> getActiveProblems() {
        if (authToken == null) login();

        Map<String, Object> request = Map.of(
                "jsonrpc", "2.0",
                "method", "problem.get",
                "params", Map.of("recent", "true", "sortfield", "eventid", "sortorder", "DESC"),
                "id", 2,
                "auth", authToken
        );

        Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);
        return (List<Map<String, Object>>) response.get("result");
    }
}