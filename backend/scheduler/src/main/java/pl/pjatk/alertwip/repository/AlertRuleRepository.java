package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.alertwip.model.AlertRule;

public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {}