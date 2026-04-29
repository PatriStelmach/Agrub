package pl.pjatk.alertwip.dto;

import java.util.List;

public record PluginSaveDTO(
        String oldName,       // Potrzebne
        String name,          // Nowa nazwa (bez rozszerzenia)
        String extension,
        String code,          // Czysty kod z edytora
        String description,
        List<String> tags,
        String cronExpression,
        int severity,
        boolean active
) {}