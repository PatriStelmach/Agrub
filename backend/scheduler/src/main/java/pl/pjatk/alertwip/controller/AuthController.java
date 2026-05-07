package pl.pjatk.alertwip.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.AuthenticationRequestDTO;
import pl.pjatk.alertwip.dto.AuthenticationResponseDTO;
import pl.pjatk.alertwip.service.AuthenticationService;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(
            @RequestBody AuthenticationRequestDTO request,
            HttpServletResponse response
    ) {
        Map<String, String> tokens = authenticationService.authenticate(request);

        Cookie refreshCookie = new Cookie("refresh_token", tokens.get("refresh_token"));
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // w prod na true (wymaga https)
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(24 * 60 * 60); // 1 dzień

        response.addCookie(refreshCookie);

        return ResponseEntity.ok(new AuthenticationResponseDTO(tokens.get("access_token")));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDTO> refresh(HttpServletRequest request) {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> "refresh_token".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("No refresh token found"));

        String newAccessToken = authenticationService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(new AuthenticationResponseDTO(newAccessToken));
    }
}