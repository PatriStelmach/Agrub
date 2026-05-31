package pl.pjatk.alertwip.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pl.pjatk.alertwip.model.ProblemAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProblemActionSpecifications {

    public static Specification<ProblemAction> buildActionFilter(
            String author, LocalDate createdAt, String comment, String subject, Boolean ack) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("author"), author));

            if (createdAt != null) {
                LocalDateTime startOfDay = createdAt.atStartOfDay();
                LocalDateTime endOfDay = createdAt.plusDays(1).atStartOfDay();
                predicates.add(cb.between(root.get("createdAt"), startOfDay, endOfDay));
            }

            if (comment != null && !comment.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("message")), "%" + comment.toLowerCase() + "%"));
            }

            if (subject != null && !subject.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("problem").get("subject")), "%" + subject.toLowerCase() + "%"));
            }

            if (ack != null) {
                predicates.add(cb.equal(root.get("ackUpdate"), ack));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}