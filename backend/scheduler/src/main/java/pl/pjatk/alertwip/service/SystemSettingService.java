package pl.pjatk.alertwip.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.SystemSetting;
import pl.pjatk.alertwip.repository.SystemSettingRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SystemSettingService {

    private final SystemSettingRepository repository;

    public SystemSettingService(SystemSettingRepository repository) {
        this.repository = repository;
    }

    // Pobieranie wartości tekstowej
    //@Cacheable(value = "systemSettings", key = "#key")
    public String getValue(String key, String defaultValue) {
        return repository.findById(key)
                .map(SystemSetting::getSettingValue)
                .orElse(defaultValue);
    }

    // Pobieranie wartości logicznej (np. czy zabbix jest włączony)
    //@Cacheable(value = "systemSettingsBoolean", key = "#key")
    public boolean getBoolean(String key, boolean defaultValue) {
        return repository.findById(key)
                .map(setting -> Boolean.parseBoolean(setting.getSettingValue()))
                .orElse(defaultValue);
    }

    // Pobieranie wszystkich ustawień jako mapa
    //@Cacheable(value = "allSystemSettings")
    public Map<String, String> getAllSettings() {
        return repository.findAll().stream()
                .collect(Collectors.toMap(SystemSetting::getSettingKey, SystemSetting::getSettingValue));
    }

    // Masowy zapis z poziomu Vue
    //@CacheEvict(value = {"systemSettings", "systemSettingsBoolean", "allSystemSettings"}, allEntries = true)
    public void saveSettings(Map<String, String> settingsMap) {
        List<SystemSetting> entities = settingsMap.entrySet().stream()
                .map(entry -> new SystemSetting(entry.getKey(), entry.getValue()))
                .toList();
        repository.saveAll(entities);
    }
}