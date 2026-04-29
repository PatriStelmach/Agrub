package pl.pjatk.alertwip.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PluginDTO(
        Long id,
        String fileName,          // bez rozszerzenia
        String creator,
        String description,
        String language,     // np. ".py", ".sh", ".ps1"
        long weight,          // w KB
        List<String> tags,
        String cronExpression,
        LocalDateTime updatedAt,
        boolean active,
        int severity,
        boolean isInstalled
) {}