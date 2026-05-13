package pl.pjatk.alertwip.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.dto.AuthenticationRequestDTO;
import pl.pjatk.alertwip.model.User;
import pl.pjatk.alertwip.repository.UserGroupRepository;
import pl.pjatk.alertwip.repository.UserRepository;
import pl.pjatk.alertwip.security.JwtService;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserGroupRepository groupRepository;
    private final StringRedisTemplate redisTemplate;

    public AuthenticationService(UserRepository repository, JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 PasswordEncoder passwordEncoder,
                                 UserGroupRepository groupRepository,
                                 StringRedisTemplate redisTemplate) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.groupRepository = groupRepository;
        this.redisTemplate = redisTemplate;
    }

    public Map<String, String> authenticate(AuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Nieprawidłowy login, hasło lub błąd połączenia z serwerem: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Wystąpił nieoczekiwany błąd podczas logowania.");
        }

        User user = repository.findByEmail(request.email())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(request.email());
                    newUser.setPassword("");
                    newUser.setFirstname(request.email().split("@")[0]);
                    newUser.setSurname("AD User");
                    newUser.setRole(pl.pjatk.alertwip.model.Role.TECHNICIAN);
                    newUser.setActive(true);
                    return repository.save(newUser);
                });

        return Map.of(
                "access_token", jwtService.generateAccessToken(user),
                "refresh_token", jwtService.generateRefreshToken(user)
        );
    }

    public void blacklistToken(String token) {
        try {
            Date expiryDate = jwtService.extractClaim(token, io.jsonwebtoken.Claims::getExpiration);
            long diffInMillis = expiryDate.getTime() - System.currentTimeMillis();

            if (diffInMillis > 0) {
                redisTemplate.opsForValue().set(token, "blacklisted", diffInMillis, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
        }
    }

    public String refreshAccessToken(String refreshToken) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(refreshToken))) {
            throw new RuntimeException("Ten token został unieważniony (wylogowano).");
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