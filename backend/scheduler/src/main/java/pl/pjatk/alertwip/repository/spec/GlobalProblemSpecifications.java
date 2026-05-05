package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.domain.Specification;
import pl.pjatk.alertwip.model.GlobalProblem;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GlobalProblemSpecifications {

    public static Specification<GlobalProblem> buildHistoryFilter(
            String searchQueryMessage, String searchQuerySubject,
            List<String> systems, String origin,
            LocalDateTime dateFrom, LocalDateTime dateTo) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Wyszukiwanie w Message (AND)
            if (searchQueryMessage != null && !searchQueryMessage.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("message")), "%" + searchQueryMessage.toLowerCase() + "%"));
            }

            // 2. Wyszukiwanie w Subject (AND)
            if (searchQuerySubject != null && !searchQuerySubject.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("subject")), "%" + searchQuerySubject.toLowerCase() + "%"));
            }

            // 3. Filtrowanie po systemach (np. WAZUH, ZABBIX)
            if (systems != null && !systems.isEmpty()) {
                predicates.add(root.get("originType").in(systems));
            }

            // 4. Wyszukiwanie po Origin / Source (Prefix Search)
            if (origin != null && !origin.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("source")), origin.toLowerCase() + "%"));
            }

            // 5. Zakres dat
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), dateTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}