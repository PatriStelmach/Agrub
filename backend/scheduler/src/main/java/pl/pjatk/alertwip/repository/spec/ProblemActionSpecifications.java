package pl.pjatk.alertwip.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pl.pjatk.alertwip.dto.AlertHistoryFiltersDTO;
import pl.pjatk.alertwip.model.ProblemAction;

import java.util.ArrayList;
import java.util.List;

public class ProblemActionSpecifications {

    public static Specification<ProblemAction> buildActionFilter(String author, AlertHistoryFiltersDTO f) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("author"), author));

            if (f != null) {
                // sprawdzamy severity powiązanego alertu
                if (f.severity() != null && !f.severity().isEmpty()) {
                    predicates.add(root.get("problem").get("severity").in(f.severity()));
                }

                // Wiadomość (komentarz akcji) i Temat (temat powiązanego alertu)
                if (f.message() != null && !f.message().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("message")), "%" + f.message().toLowerCase() + "%"));
                }
                if (f.subject() != null && !f.subject().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("problem").get("subject")), "%" + f.subject().toLowerCase() + "%"));
                }

                // source alertu
                if (f.source() != null && !f.source().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("problem").get("source")), "%" + f.source().toLowerCase() + "%"));
                }

                // origin alertu
                if (f.origin() != null && !f.origin().isEmpty()) {
                    predicates.add(root.get("problem").get("originType").in(f.origin()));
                }

                // (ACK / UNACK) - sprawdzamy, czy TA AKCJA nadała dany status
                if (Boolean.TRUE.equals(f.ack()) && !Boolean.TRUE.equals(f.unack())) {
                    predicates.add(cb.isTrue(root.get("ackUpdate")));
                } else if (Boolean.TRUE.equals(f.unack()) && !Boolean.TRUE.equals(f.ack())) {
                    predicates.add(cb.isFalse(root.get("ackUpdate")));
                }

                // Daty utworzenia AKCJI
                if (f.createdDateFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), f.createdDateFrom()));
                }
                if (f.createdDateTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), f.createdDateTo()));
                }

                // Daty zamknięcia powiązanego ALERTU
                if (f.closedDateFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("problem").get("closedAt"), f.closedDateFrom()));
                }
                if (f.closedDateTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("problem").get("closedAt"), f.closedDateTo()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}