package pl.pjatk.alertwip.repository.spec;

import org.springframework.data.jpa.domain.Specification;
import pl.pjatk.alertwip.model.GlobalProblem;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GlobalProblemSpecifications {

    public static Specification<GlobalProblem> buildHistoryFilter(
            String searchQuery, List<String> systems, String origin,
            LocalDateTime dateFrom, LocalDateTime dateTo,
            Long lastItemId, Long firstItemId) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Wyszukiwanie pełnotekstowe (message LUB subject)
            if (searchQuery != null && !searchQuery.isBlank()) {
                String pattern = "%" + searchQuery.toLowerCase() + "%";
                Predicate msgMatch = cb.like(cb.lower(root.get("message")), pattern);
                Predicate subMatch = cb.like(cb.lower(root.get("subject")), pattern);
                predicates.add(cb.or(msgMatch, subMatch));
            }

            // 2. Filtrowanie po systemach (np. WAZUH, ZABBIX) - lista IN
            if (systems != null && !systems.isEmpty()) {
                predicates.add(root.get("originType").in(systems));
            }

            // 3. Wyszukiwanie ręczne po origin (Prefix Search: origin%)
            if (origin != null && !origin.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("source")), origin.toLowerCase() + "%")); // Zmieniłem na "source" bazując na naszym starym kodzie
            }

            // 4. Zakres dat
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), dateTo));
            }

            // 5. Keyset Pagination
            if (lastItemId != null) {
                // Kliknięto "Następna strona" -> szukamy ID mniejszych (starszych) niż ostatni element
                predicates.add(cb.lessThan(root.get("id"), lastItemId));
            } else if (firstItemId != null) {
                // Kliknięto "Poprzednia strona" -> szukamy ID większych (nowszych) niż pierwszy element
                predicates.add(cb.greaterThan(root.get("id"), firstItemId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}