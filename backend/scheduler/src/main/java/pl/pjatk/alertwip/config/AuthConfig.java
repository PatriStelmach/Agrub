package pl.pjatk.alertwip.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class AuthConfig {

    @Value("${ad.domain:}")
    private String adDomain;

    @Value("${ad.url:}")
    private String adUrl;

    @Value("${ldap.baseDn:}")
    private String baseDn;

    @Value("${ldap.userDnPattern:}")
    private String userDnPattern;

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(adUrl);
        if (baseDn != null && !baseDn.isEmpty()) {
            contextSource.setBase(baseDn);
        }
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            LdapContextSource contextSource) {

        List<org.springframework.security.authentication.AuthenticationProvider> providers = new ArrayList<>();

        // 1. logowanie do Bazy Danych
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder);
        providers.add(daoProvider);

        // 2. LDAP
        if (adUrl != null && !adUrl.isEmpty()) {

            // Konfiguracja binder - to on mówi serwerowi LDAP w jakim formacie przesyła nazwę użytkownika
            BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource);

            if (userDnPattern != null && !userDnPattern.isEmpty()) {
                // Łączy login z formatem ustawionym w application.properties
                bindAuthenticator.setUserDnPatterns(new String[]{userDnPattern});
            }

            LdapAuthenticationProvider ldapProvider =
                    new LdapAuthenticationProvider(bindAuthenticator, new DefaultLdapAuthoritiesPopulator(contextSource, ""));

            providers.add(ldapProvider);
        }

        return new ProviderManager(providers);
    }
}