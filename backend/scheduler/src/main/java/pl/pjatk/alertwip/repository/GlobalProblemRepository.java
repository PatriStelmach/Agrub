package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.alertwip.model.GlobalProblem;

import java.util.List;
import java.util.Optional;

public interface GlobalProblemRepository extends JpaRepository<GlobalProblem, Long> {

    Optional<GlobalProblem> findFirstByUniqueKeyOrderByIdDesc(String uniqueKey);
    List<GlobalProblem> findAllByStatusNot(String status);
}