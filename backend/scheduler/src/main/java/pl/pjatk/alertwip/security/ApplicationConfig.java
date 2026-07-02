package pl.pjatk.alertwip.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pjatk.alertwip.repository.UserRepository;
import pl.pjatk.alertwip.service.SystemSettingService;

@Configuration
public class ApplicationConfig {

    private final UserRepository repository;
    private final SystemSettingService settingService;

    public ApplicationConfig(UserRepository repository, SystemSettingService settingService) {
        this.repository = repository;
        this.settingService = settingService;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik nie znaleziony"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("localAuthProvider")
    public AuthenticationProvider localAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean("adAuthProvider")
    public AuthenticationProvider adAuthenticationProvider() {
        try {
            String adUrl = settingService.getValue("SECURITY_AD_URL", "ldap://ldap.forumsys.com:389");
            String baseDn = settingService.getValue("SECURITY_LDAP_BASE_DN", "dc=example,dc=com");
            String userPattern = settingService.getValue("SECURITY_LDAP_USER_DN_PATTERN", "uid={0}");

            org.springframework.security.ldap.DefaultSpringSecurityContextSource contextSource =
                    new org.springframework.security.ldap.DefaultSpringSecurityContextSource(adUrl);
            contextSource.afterPropertiesSet();

            org.springframework.security.ldap.authentication.BindAuthenticator authenticator =
                    new org.springframework.security.ldap.authentication.BindAuthenticator(contextSource);
            authenticator.setUserDnPatterns(new String[]{userPattern + "," + baseDn});

            return new org.springframework.security.ldap.authentication.LdapAuthenticationProvider(authenticator);
        } catch (Exception e) {
            System.err.println("[SECURITY] Błąd konfiguracji LDAP: " + e.getMessage());
            return null;
        }
    }
}