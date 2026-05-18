package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pjatk.alertwip.model.ApiKey;

import java.util.Optional;

@Repository
public interface ApiKeysRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByTokenAndActiveTrue(String token);
}
