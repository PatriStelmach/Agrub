package pl.pjatk.alertwip.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.dto.AuthenticationRequestDTO;
import pl.pjatk.alertwip.model.BlacklistedToken;
import pl.pjatk.alertwip.model.User;
import pl.pjatk.alertwip.repository.BlacklistedTokenRepository;
import pl.pjatk.alertwip.repository.UserRepository;
import pl.pjatk.alertwip.repository.UserGroupRepository;
import pl.pjatk.alertwip.security.JwtService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserGroupRepository groupRepository;
    private final BlacklistedTokenRepository tokenRepository;

    public AuthenticationService(UserRepository repository, JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 PasswordEncoder passwordEncoder,
                                 UserGroupRepository groupRepository,
                                 BlacklistedTokenRepository tokenRepository) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.groupRepository = groupRepository;
        this.tokenRepository = tokenRepository;
    }

    public Map<String, String> authenticate(AuthenticationRequestDTO request) {
        try {
            // uderza równolegle do bazy danych i do Active Directory
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Nieprawidłowy login, hasło lub błąd połączenia z serwerem: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Wystąpił nieoczekiwany błąd podczas logowania.");
        }

        // Jeśli logowanie się powiodło, szukamy usera u nas w bazie.
        // Jeśli go nie ma (bo loguje się pierwszy raz przez AD), to automatycznie tworzymy mu lokalny profil.
        User user = repository.findByEmail(request.email())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(request.email());
                    newUser.setPassword(""); // Hasło puste, bo jest zarządzane przez AD
                    newUser.setFirstname(request.email().split("@")[0]);
                    newUser.setSurname("AD User");
                    // Ustaw domyślną rolę dla nowych osób z AD
                    newUser.setRole(pl.pjatk.alertwip.model.Role.TECHNICIAN);
                    newUser.setActive(true);
                    return repository.save(newUser);
                });

        return Map.of(
                "access_token", jwtService.generateAccessToken(user),
                "refresh_token", jwtService.generateRefreshToken(user)
        );
    }

    public void logout(String token) {
        Date expiryDate = jwtService.extractClaim(token, io.jsonwebtoken.Claims::getExpiration);
        LocalDateTime expiry = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        BlacklistedToken blacklistedToken = new BlacklistedToken(token, expiry);
        tokenRepository.save(blacklistedToken);
    }

    public String refreshAccessToken(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);
        User user = repository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (jwtService.isTokenValid(refreshToken, user)) {
            return jwtService.generateAccessToken(user);
        }
        throw new RuntimeException("Invalid Refresh Token");
    }
}