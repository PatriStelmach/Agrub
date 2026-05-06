package pl.pjatk.alertwip.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.List;

public record AlertHistoryFiltersDTO(
        List<Integer> severity,
        String message,
        String subject,
        String source,
        List<String> origin,
        Boolean ack,
        Boolean unack,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdDateFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdDateTo,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime closedDateFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime closedDateTo
) {}