package pl.pjatk.alertwip.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.pjatk.alertwip.model.SystemSetting;
import pl.pjatk.alertwip.repository.SystemSettingRepository;

import java.util.Map;

@Component
public class SettingsSeeder implements CommandLineRunner {

    private final SystemSettingRepository repository;

    public SettingsSeeder(SystemSettingRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        // 1. Definiujemy nasz słownik wartości domyślnych
        // TODO: zmienić to potem z tego mocka
        Map<String, String> defaultSettings = Map.of(
                "zabbix_enabled", "true",
                "zabbix_url", "http://localhost:10000/api/mock/zabbix",
                "zabbix_api_token", "asdasd",

                "wazuh_enabled", "true",
                "wazuh_url", ""

                //  Tu dodawać nowe ustawienia
        );

        // 2. Przechodzimy przez każdy klucz
        defaultSettings.forEach((key, value) -> {
            // Jeśli w bazie NIE MA jeszcze takiego klucza, zapisujemy go
            if (!repository.existsById(key)) {
                repository.save(new SystemSetting(key, value));
                System.out.println("[SYSTEM] Dodano domyślne ustawienie do bazy: " + key);
            }
        });
    }
}