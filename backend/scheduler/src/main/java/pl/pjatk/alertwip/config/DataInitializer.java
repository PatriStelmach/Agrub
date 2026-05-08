package pl.pjatk.alertwip.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.pjatk.alertwip.model.Plugin;
import pl.pjatk.alertwip.repository.PluginRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PluginRepository pluginRepository;

    public DataInitializer(PluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
    }

    @Override
    public void run(String... args) {
        if (pluginRepository.count() > 0) {
            return;
        }

        List<String> creators = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry");
        List<String> languages = Arrays.asList(".py", ".sh", ".bash", ".ps1", ".psm1");
        List<String> tagPool = Arrays.asList("monitoring", "backup", "security", "network", "database", "web", "automation", "reporting", "alert", "sync");
        List<String> descriptionTemplates = Arrays.asList(
                "Plugin do monitorowania %s",
                "Skrypt automatyzujący %s",
                "Narzędzie do zarządzania %s",
                "Rozwiązanie dla %s",
                "System %s w tle"
        );

        List<Plugin> plugins = new ArrayList<>();
        for (int i = 1; i <= 300; i++) {
            Plugin plugin = new Plugin();
            plugin.setName("Plugin_" + i);
            plugin.setSeverity((i % 5) + 1);
            plugin.setCreator(creators.get(i % creators.size()));
            plugin.setLanguage(languages.get(i % languages.size()));
            plugin.setActive(i % 3 != 0);
            plugin.setDescription(String.format(descriptionTemplates.get(i % descriptionTemplates.size()), "systemu " + i));

            List<String> tags = new ArrayList<>();
            tags.add(tagPool.get(i % tagPool.size()));
            tags.add(tagPool.get((i + 1) % tagPool.size()));
            if (i % 2 == 0) {
                tags.add(tagPool.get((i + 2) % tagPool.size()));
            }
            plugin.setTags(tags);

            plugin.setCode("# Sample code for Plugin_" + i + "\nprint('Hello from plugin " + i + "')");

            plugins.add(plugin);
        }

        pluginRepository.saveAll(plugins);
        System.out.println("Dodano 300 rekordów do tabeli plugin.");
    }
}
