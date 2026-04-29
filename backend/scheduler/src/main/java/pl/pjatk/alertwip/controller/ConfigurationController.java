package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.service.SystemSettingService;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class ConfigurationController {

    private final SystemSettingService settingService;

    public ConfigurationController(SystemSettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public Map<String, String> getAllSettings() {
        return settingService.getAllSettings();
    }

    @PostMapping
    public ResponseEntity<String> saveSettings(@RequestBody Map<String, String> settings) {
        settingService.saveSettings(settings);
        return ResponseEntity.ok("Ustawienia zostały zapisane.");
    }
}