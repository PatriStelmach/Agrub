package pl.pjatk.alertwip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pl.pjatk.alertwip.model.Plugin;

import java.util.List;

public interface PluginRepository extends JpaRepository<Plugin, Long>, JpaSpecificationExecutor<Plugin> {

    //endpoint tylko do tagów
    @Query("SELECT DISTINCT t FROM Plugin p JOIN p.tags t")
    List<String> findAllUniqueTags();
}