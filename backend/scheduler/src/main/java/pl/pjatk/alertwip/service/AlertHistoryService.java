package pl.pjatk.alertwip.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.repository.spec.GlobalProblemSpecifications;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class AlertHistoryService {

    private final GlobalProblemRepository repository;

    public AlertHistoryService(GlobalProblemRepository repository) {
        this.repository = repository;
    }

    public List<GlobalProblem> getAlertHistory(
            int pageSize, Long lastItemId, Long firstItemId,
            String searchQuery, List<String> systems, String origin,
            LocalDateTime dateFrom, LocalDateTime dateTo) {

        // Budujemy specyfikację (warunki zapytania)
        Specification<GlobalProblem> spec = GlobalProblemSpecifications.buildHistoryFilter(
                searchQuery, systems, origin, dateFrom, dateTo, lastItemId, firstItemId
        );

        // Sortowanie i paginacja
        Sort sort;
        if (firstItemId != null) {
            // Jeśli idziemy WSTECZ, musimy pobrać najstarsze z nowszych (rosnąco)
            sort = Sort.by(Sort.Direction.ASC, "id");
        } else {
            // Domyślnie idziemy DO PRZODU, pobieramy najnowsze ze starszych (malejąco)
            sort = Sort.by(Sort.Direction.DESC, "id");
        }

        Pageable pageable = PageRequest.of(0, pageSize, sort);

        // Pobieramy dane z bazy
        List<GlobalProblem> results = repository.findAll(spec, pageable).getContent();

        // Jeśli szliśmy WSTECZ, musimy odwrócić listę, aby front nadal dostał je posortowane malejąco
        if (firstItemId != null) {
            // Musimy stowrzyć nową listę, bo z getContent() może wrócić lista unmodifiable
            List<GlobalProblem> mutableResults = new java.util.ArrayList<>(results);
            Collections.reverse(mutableResults);
            return mutableResults;
        }

        return results;
    }
}