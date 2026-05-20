package pl.pjatk.alertwip.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
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
        String url = settingService.getValue("nagios_url", "") + "?query=servicelist&servicestatus=critical+warning&details=true";
        return callNagios(url);
    }

    public String fetchHosts() {
        if (!settingService.getBoolean("nagios_enabled", false)) return null;
        String url = settingService.getValue("nagios_url", "") + "?query=hostlist";
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
}