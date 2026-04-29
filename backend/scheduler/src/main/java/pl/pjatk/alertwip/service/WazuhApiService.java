package pl.pjatk.alertwip.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class WazuhApiService {

    private final RestTemplate restTemplate;
    private final SystemSettingService settings;

    public WazuhApiService(RestTemplate restTemplate, SystemSettingService settings) {
        this.restTemplate = restTemplate;
        this.settings = settings;
    }

    @SuppressWarnings("unchecked")
    public String getAuthToken() {
        String apiUrl = settings.getValue("wazuh_url", "");
        String user = settings.getValue("wazuh_user", "admin");
        String pass = settings.getValue("wazuh_password", "");

        if (apiUrl.isBlank() || pass.isBlank()) {
            System.err.println("[WAZUH API] Niepełna konfiguracja w bazie (wazuh_url lub wazuh_password jest puste)!");
            return null;
        }

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
        String apiUrl = settings.getValue("wazuh_url", "");

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
        String apiUrl = settings.getValue("wazuh_url", "");

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