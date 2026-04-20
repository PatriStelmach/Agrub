package pl.pjatk.alertwip.dto;

public record TaskRequestDTO(
        String taskName,
        String scriptName,
        boolean isLog,
        Integer seconds,         // Dla trybu Basic
        String cronExpression    // Dla trybu Advanced
) {}