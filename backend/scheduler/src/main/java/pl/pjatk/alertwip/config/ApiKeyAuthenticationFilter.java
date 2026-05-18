package pl.pjatk.alertwip.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.pjatk.alertwip.service.ApiKeysService;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeysService apiKeysService;
    private static final String API_KEY_HEADER = "X-API-KEY";

    public ApiKeyAuthenticationFilter(ApiKeysService apiKeysService) {
        this.apiKeysService = apiKeysService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestApiKey = request.getHeader(API_KEY_HEADER);

        // Logi bo mnie popierdoli zaraz
        System.out.println("[DIAGNOSTYKA] Sprawdzam URI: " + request.getRequestURI());
        System.out.println("[DIAGNOSTYKA] Czy nagłówek X-API-KEY dotarł? " + (requestApiKey != null ? "TAK (" + requestApiKey + ")" : "NIE"));

        // Sprawdzamy czy jest klucz w bazie
        if (requestApiKey != null && apiKeysService.isValidKey(requestApiKey)) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    "Dynamic_API_Client",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_API_CLIENT"))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("[SECURITY] Dynamiczny API KEY autoryzowany pomyślnie dla: " + request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}