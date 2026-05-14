package pl.pjatk.alertwip.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.User;
import pl.pjatk.alertwip.service.SystemSettingService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final SystemSettingService settingService;

    public JwtService(SystemSettingService settingService) {
        this.settingService = settingService;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(User user) {
        long expirationMins = 15L;
        Map<String, String> settings = settingService.getAllSettings();
        if (settings != null && settings.containsKey("SECURITY_ACCESS_TOKEN_EXP_MINUTES")) {
            try {
                expirationMins = Long.parseLong(settings.get("SECURITY_ACCESS_TOKEN_EXP_MINUTES"));
            } catch (NumberFormatException ignored) {}
        }
        long jwtExpiration = expirationMins * 60 * 1000;

        return Jwts.builder()
                .claim("firstname", user.getFirstname())
                .claim("surname", user.getSurname())
                .claim("role", user.getRole().name())
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        long expirationHours = 168L;
        Map<String, String> settings = settingService.getAllSettings();
        if (settings != null && settings.containsKey("SECURITY_REFRESH_TOKEN_EXP_HOURS")) {
            try {
                expirationHours = Long.parseLong(settings.get("SECURITY_REFRESH_TOKEN_EXP_HOURS"));
            } catch (NumberFormatException ignored) {}
        }
        long refreshExpiration = expirationHours * 60 * 60 * 1000;

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}