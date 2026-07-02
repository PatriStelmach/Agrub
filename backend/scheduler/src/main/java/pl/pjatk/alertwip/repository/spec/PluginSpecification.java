package pl.pjatk.alertwip.repository.spec;

import org.springframework.data.jpa.domain.Specification;
import pl.pjatk.alertwip.model.Plugin;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class PluginSpecification {

    public static Specification<Plugin> filterLibrary(
            String name, String creator, String language, List<String> tags, Integer maxWeightKb) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (creator != null && !creator.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("creator")), "%" + creator.toLowerCase() + "%"));
            }

            if (language != null && !language.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("language")), "%" + language.toLowerCase() + "%"));
            }

            // AND dla tagów
            if (tags != null && !tags.isEmpty()) {
                for (String tag : tags) {
                    predicates.add(cb.isMember(tag, root.get("tags")));
                }
            }

            // Obliczamy wagę tak na odpierdol
            if (maxWeightKb != null && maxWeightKb != 0) {
                int maxChars = maxWeightKb * 1024;
                predicates.add(cb.lessThanOrEqualTo(cb.length(root.get("code")), maxChars));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}