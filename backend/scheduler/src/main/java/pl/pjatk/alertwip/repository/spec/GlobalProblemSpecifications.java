package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.domain.Specification;
import pl.pjatk.alertwip.dto.AlertHistoryFiltersDTO;
import pl.pjatk.alertwip.model.GlobalProblem;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class GlobalProblemSpecifications {

    public static Specification<GlobalProblem> buildHistoryFilter(AlertHistoryFiltersDTO f) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // TWARDY WYMÓG: W historii pokazujemy tylko alerty, które mają ustawioną datę zamknięcia
            predicates.add(cb.isNotNull(root.get("closedAt")));

            if (f != null) {
                // 1. Severity (Multi-select)
                if (f.severity() != null && !f.severity().isEmpty()) {
                    predicates.add(root.get("severity").in(f.severity()));
                }

                // 2. Wiadomość i Temat (LIKE)
                if (f.message() != null && !f.message().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("message")), "%" + f.message().toLowerCase() + "%"));
                }
                if (f.subject() != null && !f.subject().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("subject")), "%" + f.subject().toLowerCase() + "%"));
                }

                // 3. Source - pojedynczy String (LIKE)
                if (f.source() != null && !f.source().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("source")), "%" + f.source().toLowerCase() + "%"));
                }

                // 4. Origin - Lista (IN)
                if (f.origin() != null && !f.origin().isEmpty()) {
                    predicates.add(root.get("originType").in(f.origin()));
                }

                // 5. Potwierdzenie (ACK / UNACK)
                if (Boolean.TRUE.equals(f.ack()) && !Boolean.TRUE.equals(f.unack())) {
                    predicates.add(cb.isTrue(root.get("acknowledged")));
                } else if (Boolean.TRUE.equals(f.unack()) && !Boolean.TRUE.equals(f.ack())) {
                    predicates.add(cb.isFalse(root.get("acknowledged")));
                }

                // 6. Daty Utworzenia
                if (f.createdDateFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), f.createdDateFrom()));
                }
                if (f.createdDateTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), f.createdDateTo()));
                }

                // 7. Daty Zamknięcia z filtrów (jeśli użytkownik chce zawęzić wyszukiwanie)
                if (f.closedDateFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("closedAt"), f.closedDateFrom()));
                }
                if (f.closedDateTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("closedAt"), f.closedDateTo()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}