package pl.pjatk.alertwip.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class NagiosApiService {

    private final RestTemplate restTemplate;
    private final SystemSettingService settingService;

    public NagiosApiService(RestTemplate restTemplate, SystemSettingService settingService) {
        this.restTemplate = restTemplate;
        this.settingService = settingService;
    }

    public String fetchAlerts() {
        if (!settingService.getBoolean("nagios_enabled", false)) return null;
        String url = settingService.getValue("nagios_url", "") + "/statusjson.cgi?query=servicelist&servicestatus=critical+warning&details=true";
        return callNagios(url);
    }

    public String fetchHosts() {
        if (!settingService.getBoolean("nagios_enabled", false)) return null;
        String url = settingService.getValue("nagios_url", "") + "/statusjson.cgi?query=hostlist";
        return callNagios(url);
    }

    private String callNagios(String url) {
        String user = settingService.getValue("nagios_user", "");
        String pass = settingService.getValue("nagios_password_SECRET", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(user, pass);

        try {
            return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        } catch (Exception e) {
            throw new RuntimeException("Błąd API Nagios: " + e.getMessage());
        }
    }

    public void sendCgiCommand(MultiValueMap<String, String> body) {
        if (!settingService.getBoolean("nagios_enabled", false)) return;

        String cgiUrl = settingService.getValue("nagios_cgi_url", "") + "/cmd.cgi";
        String user = settingService.getValue("nagios_user", "");
        String pass = settingService.getValue("nagios_password_SECRET", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(user, pass);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(cgiUrl, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Nieoczekiwany kod błędu od Nagiosa (cmd.cgi): " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Błąd wysyłania komendy ACK do Nagiosa: " + e.getMessage());
        }
    }
}