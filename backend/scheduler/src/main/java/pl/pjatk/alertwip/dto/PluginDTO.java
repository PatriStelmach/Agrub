package pl.pjatk.alertwip.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PluginDTO(
        Long id,
        String name,          // bez rozszerzenia
        String creator,
        String description,
        String extension,     // np. ".py", ".sh", ".ps1"
        long weight,          // w KB
        List<String> tags,
        String cronExpression,
        LocalDateTime updatedAt,
        boolean active,
        boolean isLog,
        boolean isInstalled
) {}