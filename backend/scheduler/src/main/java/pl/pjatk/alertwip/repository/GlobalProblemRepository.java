package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.pjatk.alertwip.model.GlobalProblem;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Set;

import java.util.List;
import java.util.Optional;

public interface GlobalProblemRepository extends JpaRepository<GlobalProblem, Long>, JpaSpecificationExecutor<GlobalProblem> {

    Optional<GlobalProblem> findFirstByUniqueKeyOrderByIdDesc(String uniqueKey);
    List<GlobalProblem> findAllByStatusNot(String status);

    boolean existsByUniqueKey(String uniqueKey);

    List<GlobalProblem> findBySource(String sourceName);

    long countByStatus(String status);
    long countBySeverityGreaterThanEqual(int severity);

    List<GlobalProblem> findByStatusNot(String status);

    @Query("SELECT p.uniqueKey FROM GlobalProblem p WHERE p.originType = 'WAZUH'")
    Set<String> findAllWazuhUniqueKeys();

    List<GlobalProblem> findByOriginTypeAndStatusNot(String originType, String status);

    List<GlobalProblem> findByOriginType(String nagios);
    List<GlobalProblem> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}