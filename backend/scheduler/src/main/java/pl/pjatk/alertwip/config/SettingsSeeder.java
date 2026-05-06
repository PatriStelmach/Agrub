package pl.pjatk.alertwip.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.pjatk.alertwip.model.Role;
import pl.pjatk.alertwip.model.SystemSetting;
import pl.pjatk.alertwip.model.User;
import pl.pjatk.alertwip.repository.SystemSettingRepository;
import pl.pjatk.alertwip.repository.UserRepository;

import java.util.Map;

@Component
public class SettingsSeeder implements CommandLineRunner {

    private final SystemSettingRepository repository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SettingsSeeder(SystemSettingRepository repository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Definiujemy nasz słownik wartości domyślnych
        // TODO: zmienić to potem z tego mocka
        Map<String, String> defaultSettings = Map.ofEntries(
                Map.entry("zabbix_enabled", "true"),
                Map.entry("zabbix_url", "http://localhost:10000/api/mock/zabbix"),
                Map.entry("zabbix_api_token", "asdasd"),

                Map.entry("wazuh_enabled", "false"),
                Map.entry("wazuh_url", "https://localhost:55000"),
                Map.entry("wazuh_user", "admin"),
                Map.entry("wazuh_password", ""),
                Map.entry("wazuh_min_active_level", "8"),

                Map.entry("nagios_enabled", "true"),
                Map.entry("nagios_url", "http://localhost:8080/nagios/cgi-bin/statusjson.cgi"),
                Map.entry("nagios_user", "nagiosadmin"),
                Map.entry("nagios_pass", "nagiosadmin")

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

        // Testowy uzytkownik
        if (userRepository.findByEmail("admin@pjatk.pl").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@pjatk.pl");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstname("Admin");
            admin.setSurname("Systemu");
            admin.setRole(Role.ADMINISTRATOR);
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("[SECURITY] Utworzono testowego użytkownika: admin@pjatk.pl");
        }
    }
}