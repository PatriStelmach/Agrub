package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.alertwip.model.UserGroup;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {}