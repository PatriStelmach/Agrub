package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.pjatk.alertwip.model.Plugin;
import java.util.List;

public interface PluginRepository extends JpaRepository<Plugin, Long> {

    @Query(value = "SELECT * FROM plugin p WHERE " +
            "(:name IS NULL OR p.name LIKE %:name%) AND " +
            "(:creator IS NULL OR p.creator = :creator) AND " +
            "(:lang IS NULL OR p.language = :lang) AND " +
            "p.id >= :firstId " + // Szukamy od tego ID w górę
            "ORDER BY p.id ASC LIMIT :pageSize", nativeQuery = true)
    List<Plugin> findLibrary(
            @Param("name") String name,
            @Param("creator") String creator,
            @Param("lang") String lang,
            @Param("firstId") int firstId,
            @Param("pageSize") int pageSize);
}