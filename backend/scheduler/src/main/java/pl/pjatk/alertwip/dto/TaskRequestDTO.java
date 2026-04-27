package pl.pjatk.alertwip.dto;

public record TaskRequestDTO(
        String taskName,
        String scriptName,
        Integer seconds,         // Dla trybu Basic
        String cronExpression    // Dla trybu Advanced
) {}