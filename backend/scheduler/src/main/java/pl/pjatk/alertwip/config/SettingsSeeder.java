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
        Map<String, String> defaultSettings = Map.of(
                "zabbix_enabled", "false",
                "zabbix_url", "http://192.168.1.100/zabbix/api_jsonrpc.php",
                "zabbix_api_token", ""
                //  Tu dodawać nowe ustawienia
                // "email_notifications_enabled", "false",
                // "system_language", "PL"
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