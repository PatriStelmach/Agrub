package pl.pjatk.alertwip.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.dto.AuthenticationRequestDTO;
import pl.pjatk.alertwip.model.Role;
import pl.pjatk.alertwip.model.User;
import pl.pjatk.alertwip.model.UserGroup;
import pl.pjatk.alertwip.repository.UserGroupRepository;
import pl.pjatk.alertwip.repository.UserRepository;
import pl.pjatk.alertwip.security.JwtService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final UserGroupRepository userGroupRepository;
    private final JwtService jwtService;
    private final AuthenticationProvider localAuthProvider;
    private final AuthenticationProvider adAuthProvider;
    private final StringRedisTemplate redisTemplate;
    private final SystemSettingService settingService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository repository,
                                 UserGroupRepository userGroupRepository,
                                 JwtService jwtService,
                                 @Qualifier("localAuthProvider") AuthenticationProvider localAuthProvider,
                                 @Qualifier("adAuthProvider") AuthenticationProvider adAuthProvider,
                                 StringRedisTemplate redisTemplate,
                                 SystemSettingService settingService,
                                 EmailService emailService,
                                 PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.userGroupRepository = userGroupRepository;
        this.jwtService = jwtService;
        this.localAuthProvider = localAuthProvider;
        this.adAuthProvider = adAuthProvider;
        this.redisTemplate = redisTemplate;
        this.settingService = settingService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, String> authenticateLocal(AuthenticationRequestDTO request) {
        try {
            localAuthProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Nieprawidłowy login lub hasło");
        }

        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik uwierzytelniony, ale nie znaleziony w bazie"));

        checkPasswordExpiration(user);

        return Map.of(
                "access_token", jwtService.generateAccessToken(user),
                "refresh_token", jwtService.generateRefreshToken(user)
        );
    }

    public Map<String, String> authenticateAd(AuthenticationRequestDTO request) {
        if (adAuthProvider == null) {
            throw new RuntimeException("Konfiguracja AD jest błędna lub nieaktywna.");
        }

        try {
            adAuthProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Nieprawidłowy login lub hasło do Active Directory");
        }

        User user = repository.findByEmail(request.email()).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(request.email());
            newUser.setPassword("EXTERNAL_AD_AUTH");

            String[] parts = request.email().split("@")[0].split("\\.");
            if (parts.length >= 2) {
                newUser.setFirstname(capitalize(parts[0]));
                newUser.setSurname(capitalize(parts[1]));
            } else {
                newUser.setFirstname(capitalize(request.email().split("@")[0]));
                newUser.setSurname("AD");
            }

            newUser.setRole(Role.TECHNICIAN);
            newUser.setActive(true);

            userGroupRepository.findByName("TECH").ifPresent(techGroup -> newUser.getGroups().add(techGroup));

            return repository.save(newUser);
        });

        checkPasswordExpiration(user);

        return Map.of(
                "access_token", jwtService.generateAccessToken(user),
                "refresh_token", jwtService.generateRefreshToken(user)
        );
    }

    private void checkPasswordExpiration(User user) {
        if (user.getLastPasswordChangeDate() != null && !"EXTERNAL_AD_AUTH".equals(user.getPassword())) {
            long passwordLifetimeDays = Long.parseLong(settingService.getValue("SECURITY_PASSWORD_LIFETIME_DAYS", "90"));

            if (user.getLastPasswordChangeDate().plusDays(passwordLifetimeDays).isBefore(LocalDateTime.now())) {
                throw new CredentialsExpiredException("Twoje hasło wygasło. Wymagana zmiana hasła.");
            }
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
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

        SecureRandom random = new SecureRandom();
        String token = String.format("%06d", random.nextInt(1_000_000));
        redisTemplate.opsForValue().set("reset:" + token, email, 15, TimeUnit.MINUTES);
        String message = "Hello " + user.getFirstname() + ",\n\n" +
                "Copy this token to reset your password (valid for 15 minutes):\n" + token;
        emailService.sendSimpleMessage(email, "Password reset - AlertVIP", message);
    }

    public void completePasswordReset(String token, String newPassword) {
        String email = redisTemplate.opsForValue().get("reset:" + token);
        if (email == null) {
            throw new RuntimeException("Token expired or is invalid");
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