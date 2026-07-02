package pl.pjatk.alertwip.dto;

import java.util.Map;

public record AlertsBySeverityDTO(Long x, Map<Integer, Long> severities) {}