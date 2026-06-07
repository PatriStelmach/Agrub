package pl.pjatk.alertwip.dto;

import java.time.LocalDateTime;

public record GlobalProblemHistoryDTO(
        Long id,
        String source,
        String message,
        String originType,
        String subject,
        Integer severity,
        boolean isAcknowledged,
        LocalDateTime closedAt,
        LocalDateTime createdAt
) {
}
