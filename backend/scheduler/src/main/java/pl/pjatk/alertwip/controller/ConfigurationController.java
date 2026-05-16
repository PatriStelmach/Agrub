package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.service.SystemSettingService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/settings")
public class ConfigurationController {

    private static final List<String> SUPPORTED_SYSTEMS = List.of("zabbix", "nagios", "wazuh");
    private final SystemSettingService settingService;

    public ConfigurationController(SystemSettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public Map<String, String> getAllSettings() {
        System.out.println(settingService.getAllSettings());
        return settingService.getAllSettings();
    }

    @GetMapping("/enabled")
    public ResponseEntity<Map<String, String>> getEnabledSystems() {
        Map<String, String> allSettings = settingService.getAllSettings();

        Map<String, String> enabledSettings = SUPPORTED_SYSTEMS.stream()
                .map(system -> system.toLowerCase() + "_enabled")
                .filter(allSettings::containsKey)
                .collect(Collectors.toMap(
                        key -> key,
                        allSettings::get
                ));

        return ResponseEntity.ok(enabledSettings);
    }

    @GetMapping("/{system}")
    public ResponseEntity<Map<String, String>> getSystemSettings(@PathVariable String system) {
        String lowerCaseSystem = system.toLowerCase();

        if (!SUPPORTED_SYSTEMS.contains(lowerCaseSystem)) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, String> allSettings = settingService.getAllSettings();
        String systemPrefix = lowerCaseSystem + "_";
        Map<String, String> systemSettings = allSettings.entrySet().stream()
                .filter(entry -> entry.getKey().toLowerCase().startsWith(systemPrefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return ResponseEntity.ok(systemSettings);
    }

    @PostMapping
    public ResponseEntity<String> saveSettings(@RequestBody Map<String, String> settings) {
        settingService.saveSettings(settings);
        return ResponseEntity.ok("Ustawienia zostały zapisane.");
    }

    @GetMapping("/security")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Map<String, String>> getSecuritySettings() {
        Map<String, String> settings = settingService.getAllSettings();
        return ResponseEntity.ok(Map.of(
                "access_token_exp_minutes", settings.getOrDefault("SECURITY_ACCESS_TOKEN_EXP_MINUTES", "15"),
                "refresh_token_exp_hours", settings.getOrDefault("SECURITY_REFRESH_TOKEN_EXP_HOURS", "168"),
                "password_lifetime_days", settings.getOrDefault("SECURITY_PASSWORD_LIFETIME_DAYS", "90")
        ));
    }

    @PutMapping("/security")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Map<String, String>> updateSecuritySettings(@RequestBody Map<String, String> settings) {
        settingService.saveSettings(settings);
        return ResponseEntity.ok(Map.of("message", "Ustawienia bezpieczeństwa zaktualizowane"));
    }
}