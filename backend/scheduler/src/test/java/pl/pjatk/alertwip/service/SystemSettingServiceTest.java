package pl.pjatk.alertwip.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pjatk.alertwip.model.SystemSetting;
import pl.pjatk.alertwip.repository.SystemSettingRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemSettingServiceTest {

    @Mock
    private SystemSettingRepository repository;

    @InjectMocks
    private SystemSettingService systemSettingService;

    @Test
    void shouldReturnDefaultValueWhenKeyNotFound() {
        // Given
        when(repository.findById("non_existent_key")).thenReturn(Optional.empty());

        // When
        String result = systemSettingService.getValue("non_existent_key", "default_fallback");

        // Then
        assertEquals("default_fallback", result);
    }

    @Test
    void shouldReturnActualValueWhenKeyExists() {
        // Given
        SystemSetting setting = new SystemSetting("zabbix_enabled", "true");
        when(repository.findById("zabbix_enabled")).thenReturn(Optional.of(setting));

        // When
        boolean result = systemSettingService.getBoolean("zabbix_enabled", false);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldMaskSecretValuesInGetMaskedSettings() {
        // Given
        SystemSetting publicSetting = new SystemSetting("public_url", "http://localhost");
        SystemSetting secretSetting = new SystemSetting("api_token_SECRET", "super_secret_password");

        when(repository.findAll()).thenReturn(List.of(publicSetting, secretSetting));

        // When
        Map<String, String> maskedSettings = systemSettingService.getMaskedSettings();

        // Then
        assertEquals("http://localhost", maskedSettings.get("public_url"));
        assertEquals("", maskedSettings.get("api_token_SECRET"), "Sekret powinien zostać zamaskowany pustym stringiem!");
    }
}