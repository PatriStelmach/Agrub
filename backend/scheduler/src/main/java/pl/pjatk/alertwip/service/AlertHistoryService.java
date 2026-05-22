package pl.pjatk.alertwip.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.dto.AlertHistoryFiltersDTO;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.repository.GlobalProblemSpecifications;

import java.util.List;

@Service
public class AlertHistoryService {

    private final GlobalProblemRepository repository;

    public AlertHistoryService(GlobalProblemRepository repository) {
        this.repository = repository;
    }

    public Page<GlobalProblem> getAlertHistory(
            int page, int pageSize, String sortKey, String sortOrder, AlertHistoryFiltersDTO filters) {

        // Budowa filtrów z DTO (z uwzględnieniem nałożonego z góry statusu "Done")
        Specification<GlobalProblem> spec = GlobalProblemSpecifications.buildHistoryFilter(filters);

        // Mapowanie sortowania (asc / desc z frontu)
        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primarySort = Sort.by(direction, sortKey);

        // Zabezpieczenie przed dublowaniem rekordów, jeśli daty są takie same
        Sort finalSort = primarySort.and(Sort.by(Sort.Direction.DESC, "id"));

        Pageable pageable = PageRequest.of(page, pageSize, finalSort);

        return repository.findAll(spec, pageable);
    }

    public List<String> getAllOriginTypes() {
        return repository.findDistinctOriginTypes();
    }
}