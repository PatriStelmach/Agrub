package pl.pjatk.alertwip.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.pjatk.alertwip.model.Role;
import pl.pjatk.alertwip.model.SystemSetting;
import pl.pjatk.alertwip.model.User;
import pl.pjatk.alertwip.repository.SystemSettingRepository;
import pl.pjatk.alertwip.repository.UserGroupRepository;
import pl.pjatk.alertwip.repository.UserRepository;

import java.util.Map;

@Component
public class SettingsSeeder implements CommandLineRunner {

    private final SystemSettingRepository repository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserGroupRepository userGroupRepository;

    public SettingsSeeder(SystemSettingRepository repository, UserRepository userRepository, PasswordEncoder passwordEncoder, UserGroupRepository userGroupRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userGroupRepository = userGroupRepository;
    }

    @Override
    public void run(String... args) {
        Map<String, String> defaultSettings = Map.ofEntries(
                // Zabbix
                Map.entry("zabbix_enabled", "true"),
                Map.entry("zabbix_url", "http://localhost:10000/api/mock/zabbix"),
                // to jest klucz api, a nie hasło ,ale potrzebuję mieć tak samo na froncie
                Map.entry("zabbix_api_token_SECRET", "asdasd"),

                // Wazuh
                Map.entry("wazuh_enabled", "false"),
                Map.entry("wazuh_url", "https://localhost:55000"),
                Map.entry("wazuh_user", "admin"),
                Map.entry("wazuh_password_SECRET", ""),
                Map.entry("wazuh_min_warning_level", "8"),
                Map.entry("wazuh_min_critical_level", "14"),
                Map.entry("wazuh_info_as_alerts", "false"),

                // Nagios
                Map.entry("nagios_enabled", "true"),
                Map.entry("nagios_url", "http://localhost:8080/nagios/cgi-bin/statusjson.cgi"),
                Map.entry("nagios_user", "nagiosadmin"),
                Map.entry("nagios_password_SECRET", "nagiosadmin"),

                // Sync timer
                Map.entry("external_system_sync_timer", "60"),

                // Local scripts
                Map.entry("scripts_execution_timeout_seconds", "30"),

                // SECURITY / JWT
                // 1440 min = 24h
                Map.entry("SECURITY_ACCESS_TOKEN_EXP_MINUTES", "1440"),
                // 168h = 7 dni
                Map.entry("SECURITY_REFRESH_TOKEN_EXP_HOURS", "168"),
                Map.entry("SECURITY_PASSWORD_LIFETIME_DAYS", "90"),

                // Konfiguracja AD / LDAP
                Map.entry("SECURITY_AD_DOMAIN", "example.com"),
                Map.entry("SECURITY_AD_URL", "ldap://ldap.forumsys.com:389"),
                Map.entry("SECURITY_LDAP_BASE_DN", "dc=example,dc=com"),
                Map.entry("SECURITY_LDAP_USER_DN_PATTERN", "uid={0}"),

                // Email config
                Map.entry("smtp_enabled", "false"),
                Map.entry("smtp_host", "smtp.example.com"),
                Map.entry("smtp_port", "587"),
                Map.entry("smtp_user", "alerts@example.com"),
                Map.entry("smtp_password_SECRET", "")
        );


        // 2. Przechodzimy przez każdy klucz
        defaultSettings.forEach((key, value) -> {
            // Jeśli w bazie NIE MA jeszcze takiego klucza, zapisujemy go
            if (!repository.existsById(key)) {
                repository.save(new SystemSetting(key, value));
                System.out.println("[SYSTEM] Dodano domyślne ustawienie do bazy: " + key);
            }
        });

        // Testowy uzytkownik admin
        if (userRepository.findByEmail("admin@pjatk.pl").isEmpty()) {
            pl.pjatk.alertwip.model.UserGroup adminGroup = userGroupRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        pl.pjatk.alertwip.model.UserGroup newGroup = new pl.pjatk.alertwip.model.UserGroup();
                        newGroup.setName("ADMIN");
                        return userGroupRepository.save(newGroup);
                    });

            User admin = new User();
            admin.setEmail("admin@pjatk.pl");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstname("Admin");
            admin.setSurname("Systemu");
            admin.setRole(Role.ADMINISTRATOR);
            admin.setActive(true);

            admin.getGroups().add(adminGroup);

            userRepository.save(admin);
            System.out.println("[SECURITY] Utworzono testowego użytkownika: admin@pjatk.pl w grupie ADMIN");
        }

        // Testowy uzytkownik tech
        if (userRepository.findByEmail("tech@pjatk.pl").isEmpty()) {
            pl.pjatk.alertwip.model.UserGroup techGroup = userGroupRepository.findByName("TECH")
                    .orElseGet(() -> {
                        pl.pjatk.alertwip.model.UserGroup newGroup = new pl.pjatk.alertwip.model.UserGroup();
                        newGroup.setName("TECH");
                        return userGroupRepository.save(newGroup);
                    });

            User tech = new User();
            tech.setEmail("tech@pjatk.pl");
            tech.setPassword(passwordEncoder.encode("tech"));
            tech.setFirstname("Tech");
            tech.setSurname("Systemu");
            tech.setRole(Role.ADMINISTRATOR);
            tech.setActive(true);

            tech.getGroups().add(techGroup);

            userRepository.save(tech);
            System.out.println("[SECURITY] Utworzono testowego użytkownika: tech@pjatk.pl w grupie TECH");
        }
    }
}