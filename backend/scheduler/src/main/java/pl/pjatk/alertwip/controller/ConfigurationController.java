package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.service.SystemSettingService;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class ConfigurationController {

    private static final List<String> SUPPORTED_SYSTEMS = List.of("zabbix", "nagios", "wazuh");
    private final SystemSettingService settingService;

    public ConfigurationController(SystemSettingService settingService) {
        this.settingService = settingService;
    }

    // wszystkie ustawienia
    @GetMapping
    public Map<String, String> getAllSettings() {
        return settingService.getAllSettings();
    }

    //daje któe systemy są włączone
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

    //ustawienia tylko dla danego systemu np zabbix
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

    //zmiana/dodanie ustawień
    @PostMapping
    public ResponseEntity<String> saveSettings(@RequestBody Map<String, String> settings) {
        settingService.saveSettings(settings);
        return ResponseEntity.ok("Ustawienia zostały zapisane.");
    }
}