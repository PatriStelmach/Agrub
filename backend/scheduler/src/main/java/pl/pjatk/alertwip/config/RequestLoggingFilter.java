package pl.pjatk.alertwip.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Wyciągamy metodę (GET/POST) i adres endpointu
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // Ignorujemy zapytania OPTIONS (tzw. pre-flight requests od przeglądarki dla CORS),
        // żeby nie śmiecić w konsoli, chyba że chcesz je widzieć.
        if (!method.equalsIgnoreCase("OPTIONS")) {
            System.out.println("[API REQUEST] " + method + " " + uri);
        }

        // Puszczamy zapytanie dalej do Twoich kontrolerów
        filterChain.doFilter(request, response);

        // Opcjonalnie: logowanie statusu odpowiedzi po wykonaniu
        // System.out.println("[API RESPONSE] " + method + " " + uri + " -> Status: " + response.getStatus());
    }
}