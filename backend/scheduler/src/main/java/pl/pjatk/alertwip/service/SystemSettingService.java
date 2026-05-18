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

    private static final String SECRET_SUFFIX = "_SECRET";
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
    // UWAGA, tu jbc olewwa sekrety i daje ich wartość, TYLKO DO UŻYTKU WEWNĘTRZEGO
    //@Cacheable(value = "allSystemSettings")
    public Map<String, String> getAllSettings() {
        return repository.findAll().stream()
                .collect(Collectors.toMap(SystemSetting::getSettingKey, SystemSetting::getSettingValue));
    }

    //tutaj to samo co wyżej ale z zablokowanymi wartościami SECRET
    public Map<String, String> getMaskedSettings() {
        return getAllSettings().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getKey().toUpperCase().endsWith(SECRET_SUFFIX) ? "" : entry.getValue()
                ));
    }

    // Masowy zapis z poziomu Vue
    //@CacheEvict(value = {"systemSettings", "systemSettingsBoolean", "allSystemSettings"}, allEntries = true)
    public void saveSettings(Map<String, String> settingsMap) {
        // Pobieramy aktualny stan z bazy
        Map<String, String> currentSettings = getAllSettings();

        List<SystemSetting> entities = settingsMap.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    String newValue = entry.getValue();

                    // Jeśli to sekret i przyszedł jako "" ignorujemy zmianę
                    if (key.toUpperCase().endsWith(SECRET_SUFFIX) && (newValue == null || newValue.trim().isEmpty())) {
                        newValue = currentSettings.getOrDefault(key, "");
                    }

                    return new SystemSetting(key, newValue);
                })
                .toList();

        repository.saveAll(entities);
    }
}