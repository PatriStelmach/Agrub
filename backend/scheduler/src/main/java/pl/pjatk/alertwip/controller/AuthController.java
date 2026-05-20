package pl.pjatk.alertwip.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.AuthenticationRequestDTO;
import pl.pjatk.alertwip.service.AuthenticationService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login/local")
    public ResponseEntity<?> loginLocal(@RequestBody AuthenticationRequestDTO request) {
        return processLogin(() -> authenticationService.authenticateLocal(request));
    }

    @PostMapping("/login/ad")
    public ResponseEntity<?> loginAd(@RequestBody AuthenticationRequestDTO request) {
        return processLogin(() -> authenticationService.authenticateAd(request));
    }

    private ResponseEntity<?> processLogin(java.util.function.Supplier<Map<String, String>> authSupplier) {
        try {
            Map<String, String> tokens = authSupplier.get();

            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokens.get("refresh_token"))
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(Map.of("access_token", tokens.get("access_token")));

        } catch (CredentialsExpiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Brak Refresh Tokena"));
        }
        try {
            String newAccessToken = authenticationService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(Map.of("access_token", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7).trim();
            if (!jwt.isEmpty() && !jwt.equals("undefined") && !jwt.equals("null")) {
                authenticationService.blacklistToken(jwt);
            }
        }

        if (refreshToken != null && !refreshToken.isEmpty()) {
            authenticationService.blacklistToken(refreshToken.trim());
        }

        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(Map.of("message", "Wylogowano pomyślnie. Tokeny zablokowane."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        authenticationService.initiatePasswordReset(email);
        return ResponseEntity.ok(Map.of("message", "Jeśli adres istnieje w bazie, instrukcje zostały wysłane na e-mail."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody pl.pjatk.alertwip.dto.ResetPasswordRequestDTO request) {
        try {
            authenticationService.completePasswordReset(request.token(), request.newPassword());
            return ResponseEntity.ok(Map.of("message", "Hasło zostało pomyślnie zmienione."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}