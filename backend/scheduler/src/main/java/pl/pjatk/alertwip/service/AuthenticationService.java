package pl.pjatk.alertwip.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.dto.AuthenticationRequestDTO;
import pl.pjatk.alertwip.model.User;
import pl.pjatk.alertwip.repository.UserRepository;
import pl.pjatk.alertwip.security.JwtService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redisTemplate;
    private final SystemSettingService settingService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository repository,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 StringRedisTemplate redisTemplate,
                                 SystemSettingService settingService,
                                 EmailService emailService,
                                 PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
        this.settingService = settingService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, String> authenticate(AuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Nieprawidłowy login lub hasło");
        }

        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik uwierzytelniony, ale nie znaleziony w bazie"));

        if (user.getLastPasswordChangeDate() != null) {
            long passwordLifetimeDays = Long.parseLong(settingService.getValue("SECURITY_PASSWORD_LIFETIME_DAYS", "90"));
            if (user.getLastPasswordChangeDate().plusDays(passwordLifetimeDays).isBefore(LocalDateTime.now())) {
                throw new CredentialsExpiredException("Twoje hasło wygasło. Wymagana zmiana hasła.");
            }
        }

        return Map.of(
                "access_token", jwtService.generateAccessToken(user),
                "refresh_token", jwtService.generateRefreshToken(user)
        );
    }
    
    public void initiatePasswordReset(String email) {
        User user = repository.findByEmail(email).orElse(null);

        if (user == null) {
            return;
        }

        if ("EXTERNAL_AD_AUTH".equals(user.getPassword())) {
            emailService.sendSimpleMessage(email, "Odzyskiwanie hasła - Alert",
                    "Twoje konto jest powiązane z AD. Zmień hasło w ustawieniach firmowych.");
            return;
        }

        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("reset:" + token, email, 15, TimeUnit.MINUTES);

        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        String message = "Cześć " + user.getFirstname() + ",\n\n" +
                "Link do resetu hasła (ważny 15 minut):\n" + resetLink;

        emailService.sendSimpleMessage(email, "Resetowanie hasła - AlertWIP", message);
    }

    public void completePasswordReset(String token, String newPassword) {
        String email = redisTemplate.opsForValue().get("reset:" + token);

        if (email == null) {
            throw new RuntimeException("Link wygasł lub jest nieprawidłowy.");
        }

        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik nie istnieje"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordChangeDate(LocalDateTime.now());

        repository.save(user);
        redisTemplate.delete("reset:" + token);
    }

    public void blacklistToken(String token) {
        try {
            Date expiryDate = jwtService.extractClaim(token, io.jsonwebtoken.Claims::getExpiration);
            long diffInMillis = expiryDate.getTime() - System.currentTimeMillis();
            if (diffInMillis > 0) {
                redisTemplate.opsForValue().set(token, "blacklisted", diffInMillis, TimeUnit.MILLISECONDS);
            }
        } catch (Exception ignored) {}
    }

    public String refreshAccessToken(String refreshToken) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(refreshToken))) {
            throw new RuntimeException("Token unieważniony");
        }
        String userEmail = jwtService.extractUsername(refreshToken);
        User user = repository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (jwtService.isTokenValid(refreshToken, user)) {
            return jwtService.generateAccessToken(user);
        }
        throw new RuntimeException("Invalid Refresh Token");
    }
}