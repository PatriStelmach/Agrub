package pl.pjatk.alertwip.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.pjatk.alertwip.model.Plugin;
import pl.pjatk.alertwip.repository.PluginRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
        Random random = new Random();

        // Zbiór znaków do generowania losowego wypełnienia (śmieciowego kodu)
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 =+*()[]{};:";

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

            // 1. Ustalenie losowego rozmiaru w bajtach (od 1 KB do 5 KB)
            // 1 KB = 1024 znaki. Szukamy wartości w przedziale 1024 - 5120
            int targetWeight = 1024 + random.nextInt(4097);

            // 2. Budowanie właściwego kodu
            StringBuilder codeBuilder = new StringBuilder(targetWeight);
            codeBuilder.append("# Sample code for Plugin_").append(i).append("\n");
            codeBuilder.append("print('Hello from plugin ").append(i).append("')\n");
            codeBuilder.append("\n# --- START OF RANDOM PAYLOAD (Target: ").append(targetWeight).append(" bytes) ---\n# ");

            // 3. Wypełnianie reszty stringa losowymi znakami aż osiągniemy targetWeight
            while (codeBuilder.length() < targetWeight) {
                codeBuilder.append(chars.charAt(random.nextInt(chars.length())));

                // Łamanie wierszy co około 80 znaków, żeby kod wyglądał w miarę naturalnie w bazie/edytorze
                if (codeBuilder.length() % 80 == 0) {
                    codeBuilder.append("\n# ");
                }
            }

            // Ucinamy dokładnie do wyliczonego rozmiaru, jeśli pętla lekko przestrzeliła przez dodawanie nowej linii
            String finalCode = codeBuilder.substring(0, targetWeight);

            plugin.setCode(finalCode);
            plugin.setWeight(finalCode.length()); // Ręczne przypisanie wagi dla pewności

            plugins.add(plugin);
        }

        pluginRepository.saveAll(plugins);
        System.out.println("Dodano 300 rekordów do tabeli plugin (wagi z przedziału 1-5 KB).");
    }
}