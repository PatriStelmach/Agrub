package pl.pjatk.alertwip.dto;

import pl.pjatk.alertwip.model.SyncStatus;
import java.time.LocalDateTime;

public record ProblemActionDTO(
        Long id,
        Long alertId,
        String subject,
        String author,
        String message,
        LocalDateTime createdAt,
        LocalDateTime closedAt,
        Boolean ackUpdate,
        Integer previousSeverity,
        Integer newSeverity,
        SyncStatus syncStatus
) {}