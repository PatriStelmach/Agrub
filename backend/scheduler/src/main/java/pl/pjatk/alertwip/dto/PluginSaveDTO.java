package pl.pjatk.alertwip.dto;

import java.util.List;

public record PluginSaveDTO(
        String fileName,
        String name,
        String language,
        String code,
        String description,
        List<String> tags,
        String cronExpression,
        Integer severity,
        Boolean active
) {}