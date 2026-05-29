package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT DISTINCT p.originType FROM GlobalProblem p WHERE p.originType IS NOT NULL")
    List<String> findDistinctOriginTypes();

    // ile alertów w czasie
    @Query(value = "SELECT " +
            "  CASE " +
            "    WHEN :granularity = 'day' THEN UNIX_TIMESTAMP(DATE(created_at)) * 1000 " +
            "    WHEN :granularity = 'week' THEN UNIX_TIMESTAMP(DATE_SUB(DATE(created_at), INTERVAL WEEKDAY(created_at) DAY)) * 1000 " +
            "    WHEN :granularity = 'month' THEN UNIX_TIMESTAMP(STR_TO_DATE(DATE_FORMAT(created_at, '%Y-%m-01'), '%Y-%m-%d')) * 1000 " +
            "  END AS bucketTimestamp, " +
            "  COUNT(id) AS totalValue " +
            "FROM global_problem " +
            "WHERE created_at BETWEEN :start AND :end " +
            "GROUP BY bucketTimestamp " +
            "ORDER BY bucketTimestamp", nativeQuery = true)
    List<ChartDataProjection> countAlertsByGranularity(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("granularity") String granularity);

    // średni close time
    @Query(value = "SELECT " +
            "  CASE " +
            "    WHEN :granularity = 'day' THEN UNIX_TIMESTAMP(DATE(created_at)) * 1000 " +
            "    WHEN :granularity = 'week' THEN UNIX_TIMESTAMP(DATE_SUB(DATE(created_at), INTERVAL WEEKDAY(created_at) DAY)) * 1000 " +
            "    WHEN :granularity = 'month' THEN UNIX_TIMESTAMP(STR_TO_DATE(DATE_FORMAT(created_at, '%Y-%m-01'), '%Y-%m-%d')) * 1000 " +
            "  END AS bucketTimestamp, " +
            "  ROUND(AVG(TIMESTAMPDIFF(SECOND, created_at, closed_at)), 2) AS totalValue " +
            "FROM global_problem " +
            "WHERE status = 'Done' AND closed_at IS NOT NULL AND created_at BETWEEN :start AND :end " +
            "GROUP BY bucketTimestamp " +
            "ORDER BY bucketTimestamp", nativeQuery = true)
    List<ChartDataProjection> avgCloseTimeByGranularity(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("granularity") String granularity);

    // średni ack time
    @Query(value = "SELECT " +
            "  CASE " +
            "    WHEN :granularity = 'day' THEN UNIX_TIMESTAMP(DATE(created_at)) * 1000 " +
            "    WHEN :granularity = 'week' THEN UNIX_TIMESTAMP(DATE_SUB(DATE(created_at), INTERVAL WEEKDAY(created_at) DAY)) * 1000 " +
            "    WHEN :granularity = 'month' THEN UNIX_TIMESTAMP(STR_TO_DATE(DATE_FORMAT(created_at, '%Y-%m-01'), '%Y-%m-%d')) * 1000 " +
            "  END AS bucketTimestamp, " +
            "  ROUND(AVG(TIMESTAMPDIFF(SECOND, created_at, acknowledged_at)), 2) AS totalValue " +
            "FROM global_problem " +
            "WHERE (is_acknowledged = 1 OR is_acknowledged = true) AND acknowledged_at IS NOT NULL AND created_at BETWEEN :start AND :end " +
            "GROUP BY bucketTimestamp " +
            "ORDER BY bucketTimestamp", nativeQuery = true)
    List<ChartDataProjection> avgAckTimeByGranularity(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("granularity") String granularity);
}