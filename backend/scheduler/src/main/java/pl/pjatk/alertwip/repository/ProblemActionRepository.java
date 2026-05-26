package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.pjatk.alertwip.dto.ProblemActionDTO;
import pl.pjatk.alertwip.model.ProblemAction;
import java.util.List;

public interface ProblemActionRepository extends JpaRepository<ProblemAction, Long> {
    // Pobiera całą historię akcji dla danego alertu, od najnowszych do najstarszych
    @Query("select a from ProblemAction a where a.problem.id = :problemId order by a.createdAt desc")
    List<ProblemAction> findAllByProblemIdOrderByCreatedAtDesc(Long problemId);

    @Query("select new pl.pjatk.alertwip.dto.ProblemActionDTO(" +
            "  a.id, " +
            "  p.id as alertId, " +
            "  p.subject as subject, " +
            "  a.author, " +
            "  a.message, " +
            "  a.createdAt, " +
            "  p.closedAt as closedAt, " +
            "  a.ackUpdate, " +
            "  a.previousSeverity, " +
            "  a.newSeverity, " +
            "  a.syncStatus" +
            ") " +
            "from ProblemAction a " +
            "inner join a.problem p " +
            "where a.author = :author " +
            "order by a.createdAt desc")
    List<ProblemActionDTO> findByAuthorOrderByCreatedAtDesc(String author);
}