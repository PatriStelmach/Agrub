package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.alertwip.model.ProblemAction;
import java.util.List;

public interface ProblemActionRepository extends JpaRepository<ProblemAction, Long> {
    // Pobiera całą historię akcji dla danego alertu, od najstarszych do najnowszych
    List<ProblemAction> findAllByProblemIdOrderByCreatedAtAsc(Long problemId);
}