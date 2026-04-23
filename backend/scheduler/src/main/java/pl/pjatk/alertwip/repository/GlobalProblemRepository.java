package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pjatk.alertwip.model.GlobalProblem;

import java.util.Optional;

@Repository
public interface GlobalProblemRepository extends JpaRepository<GlobalProblem, Long> {

    Optional<GlobalProblem> findByTaskId(Long taskId);

    Optional<GlobalProblem> findByTaskName(String taskName);

    boolean existsByTaskId(Long taskId);

    void deleteByTaskId(Long taskId);
}