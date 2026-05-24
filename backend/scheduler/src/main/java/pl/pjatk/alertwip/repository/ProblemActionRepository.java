package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.pjatk.alertwip.model.ProblemAction;
import java.util.List;

public interface ProblemActionRepository extends JpaRepository<ProblemAction, Long> {
    // Pobiera całą historię akcji dla danego alertu, od najnowszych do najstarszych - czemu niby na odwrót? xD
    @Query("select a from ProblemAction a where a.problem.id = :problemId order by a.createdAt desc")
    List<ProblemAction> findAllByProblemIdOrderByCreatedAtDesc(Long problemId);

    List<ProblemAction> findByAuthorOrderByCreatedAtDesc(String author);
}