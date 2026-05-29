package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.alertwip.model.ApiKey;
import pl.pjatk.alertwip.repository.ApiKeysRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ApiKeysService {

    private final ApiKeysRepository repository;

    public ApiKeysService(ApiKeysRepository repository) {
        this.repository = repository;
    }

    //generowanie nowego klucza
    @Transactional
    public ApiKey generateApiKey(String description) {
        String rawToken = "alertwip_live_" + UUID.randomUUID().toString().replace("-","");

        ApiKey apiKey = new ApiKey();
        apiKey.setToken(rawToken);
        apiKey.setDescription(description);
        apiKey.setActive(true);

        return repository.save(apiKey);
    }

    public boolean getApiKey(Long id) {
        return  repository.findById(id).orElseThrow().isActive();
    }

    //Sprawdzanie tokena
    public boolean isValidKey(String token) {
        if (token == null || token.isBlank()) return false;
        return repository.findByTokenAndActiveTrue(token).isPresent();
    }

    public List<ApiKey> getAllKeys() {
        return repository.findAll();
    }

    @Transactional
    public void revokeKey(Long id) {
        repository.findById(id).ifPresent(apiKey -> {
            apiKey.setActive(!apiKey.isActive());
            repository.save(apiKey);
        });
    }

    @Transactional
    public void deleteKey(Long id) {
        repository.deleteById(id);
    }
}