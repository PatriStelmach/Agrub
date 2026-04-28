package pl.pjatk.alertwip.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class WazuhApiService {

    private final String apiUrl;
    private final String user;
    private final String pass;
    private final RestTemplate restTemplate;

    public WazuhApiService(
            @Value("${wazuh.api.url}") String apiUrl,
            @Value("${wazuh.api.user}") String user,
            @Value("${wazuh.api.password}") String pass,
            RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.user = user;
        this.pass = pass;
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public String getAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(user, pass);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl + "/security/user/authenticate",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                return (String) data.get("token");
            }
        } catch (Exception e) {
            System.err.println("[WAZUH API] Błąd autoryzacji: " + e.getMessage());
        }
        return null;
    }

    public String fetchAlerts(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        try {
            String url = apiUrl + "/manager/logs?limit=100";
            return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        } catch (Exception e) {
            System.err.println("[WAZUH API] Błąd pobierania alertów: " + e.getMessage());
            return null;
        }
    }

    public String fetchAgents(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        try {
            String url = apiUrl + "/agents?select=id,name,status,ip";
            return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        } catch (Exception e) {
            System.err.println("[WAZUH API] Błąd pobierania listy agentów: " + e.getMessage());
            return null;
        }
    }
}