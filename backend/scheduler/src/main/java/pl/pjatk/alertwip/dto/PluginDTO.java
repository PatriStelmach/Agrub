package pl.pjatk.alertwip.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PluginDTO(
        Long id,              // ID z bazy
        String fileName,      // Nazwa pliku na dysku (null w sklepie)
        String name,
        String creator,
        String description,
        String language,
        long weight,          // w KB
        List<String> tags,
        String cronExpression, // null w sklepie
        LocalDateTime updatedAt,
        boolean active,
        boolean isLog,
        boolean isInstalled   // Flaga informująca, czy mamy to już na dysku
) {}