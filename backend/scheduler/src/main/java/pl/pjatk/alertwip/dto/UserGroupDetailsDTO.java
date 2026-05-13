package pl.pjatk.alertwip.dto;

import java.util.List;

public record UserGroupDetailsDTO(
        Long id,
        String name,
        List<UserSummaryDTO> users,
        List<AlertRuleRequestDTO> rules
) {}
