package pl.pjatk.alertwip.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.dto.AuthenticationRequestDTO;
import pl.pjatk.alertwip.model.User;
import pl.pjatk.alertwip.repository.UserRepository;
import pl.pjatk.alertwip.repository.UserGroupRepository;
import pl.pjatk.alertwip.security.JwtService;

import java.util.Map;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserGroupRepository groupRepository;

    public AuthenticationService(UserRepository repository, JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 PasswordEncoder passwordEncoder,
                                 UserGroupRepository groupRepository) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.groupRepository = groupRepository;
    }

    public Map<String, String> authenticate(AuthenticationRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return Map.of(
                "access_token", jwtService.generateAccessToken(user),
                "refresh_token", jwtService.generateRefreshToken(user)
        );
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