package pl.pjatk.alertwip.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

// Ta adnotacja usuwa z JSON-a wszystkie pola, które są nullem!
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AlertUpdateEventDTO(
        Long id,              // ID samej akcji/komentarza
        Long alertId,         // ID głównego problemu (GlobalProblem)
        String author,
        Boolean ack,          // Opcjonalne (może być null)
        String message,       // Opcjonalne
        LocalDateTime createdAt,
        Integer newSeverity   // Opcjonalne
) {}