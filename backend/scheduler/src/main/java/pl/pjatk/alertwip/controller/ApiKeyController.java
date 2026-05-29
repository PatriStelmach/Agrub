package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.model.ApiKey;
import pl.pjatk.alertwip.service.ApiKeysService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/keys")
@PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
public class ApiKeyController {

    private final ApiKeysService apiKeysService;

    public ApiKeyController(ApiKeysService apiKeysService) {
        this.apiKeysService = apiKeysService;
    }

    @GetMapping
    public ResponseEntity<List<ApiKey>> listAllKeys() {
        return ResponseEntity.ok(apiKeysService.getAllKeys());
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiKey> generateKey(@RequestBody Map<String, String> payload) {
        String description = payload.getOrDefault("description", "Empty description");
        ApiKey newKey = apiKeysService.generateApiKey(description);
        return ResponseEntity.ok(newKey); // Zwróci pełen obiekt, w tym wygenerowany token "awip_live_..."
    }

    @PutMapping("/{id}/revoke")
    public ResponseEntity<String> revokeKey(@PathVariable Long id) {
        apiKeysService.revokeKey(id);
        var isRevoked = !apiKeysService.getApiKey(id);
        return ResponseEntity.ok( isRevoked ? "Successfully revoked key" : "Successfully unrevoked key");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteKey(@PathVariable Long id) {
        apiKeysService.deleteKey(id);
        return ResponseEntity.ok("Klucz trwale usunięty");
    }
}