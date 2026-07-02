package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pjatk.alertwip.model.TaskExecutionLog;

import java.util.List;

@Repository
public interface TaskExecutionLogRepository extends JpaRepository<TaskExecutionLog, Long> {
    List<TaskExecutionLog> findByTaskIdOrderByExecutionTimeDesc(Long taskId);
}