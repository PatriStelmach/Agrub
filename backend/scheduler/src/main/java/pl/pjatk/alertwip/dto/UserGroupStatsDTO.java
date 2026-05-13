package pl.pjatk.alertwip.dto;

public record UserGroupStatsDTO(
        Long id,
        String name,
        long userCount,
        long ruleCount
) {}