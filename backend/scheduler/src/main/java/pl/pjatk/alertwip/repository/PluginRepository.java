package pl.pjatk.alertwip.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.pjatk.alertwip.model.Plugin;

public interface PluginRepository extends JpaRepository<Plugin, Long> {
    @Query("SELECT p FROM Plugin p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:creator IS NULL OR p.creator = :creator) AND " +
            "(:lang IS NULL OR p.language = :lang)")
    Page<Plugin> findLibrary(
            @Param("name") String name,
            @Param("creator") String creator,
            @Param("lang") String lang,
            Pageable pageable);
}