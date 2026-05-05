package pl.pjatk.alertwip.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.repository.GlobalProblemSpecifications;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertHistoryService {

    private final GlobalProblemRepository repository;

    public AlertHistoryService(GlobalProblemRepository repository) {
        this.repository = repository;
    }

    public Page<GlobalProblem> getAlertHistory(
            int page, int pageSize, String sortBy, boolean descending,
            String searchQueryMessage, String searchQuerySubject,
            List<String> systems, String origin,
            LocalDateTime dateFrom, LocalDateTime dateTo) {

        // Budujemy specyfikację (warunki)
        Specification<GlobalProblem> spec = GlobalProblemSpecifications.buildHistoryFilter(
                searchQueryMessage, searchQuerySubject, systems, origin, dateFrom, dateTo
        );

        // Sortowanie wybrane przez użytkownika (np. po createdAt lub severity)
        Sort primarySort = Sort.by(descending ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        // Zapasowe sortowanie po ID dla stabilności bazy danych
        Sort finalSort = primarySort.and(Sort.by(Sort.Direction.DESC, "id"));

        // Tworzymy obiekt Pageable (UWAGA: Spring indeksuje strony od zera!)
        Pageable pageable = PageRequest.of(page, pageSize, finalSort);

        // Magia Springa: wygeneruje sam 2 zapytania (SELECT dane LIMIT... oraz SELECT COUNT)
        return repository.findAll(spec, pageable);
    }
}