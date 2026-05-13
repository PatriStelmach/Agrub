package pl.pjatk.alertwip.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1024, nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime blacklistedAt = LocalDateTime.now();

    public BlacklistedToken() {}

    public BlacklistedToken(String token) {
        this.token = token;
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public LocalDateTime getBlacklistedAt() { return blacklistedAt; }
}