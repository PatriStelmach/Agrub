package pl.pjatk.alertwip.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.dto.PluginDTO;
import pl.pjatk.alertwip.dto.PluginDetailsDTO;
import pl.pjatk.alertwip.model.Plugin;
import pl.pjatk.alertwip.model.ScheduledTask;
import pl.pjatk.alertwip.repository.ScheduledTaskRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class PluginManagerService {

    private final ScheduledTaskRepository taskRepository;

    @Value("${app.scripts.path}")
    private String scriptsPath;

    public PluginManagerService(ScheduledTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // --- KLASY I METODY POMOCNICZE DO PLIKÓW ---

    private static class ParsedHeaders {
        String description = "Brak opisu w pliku";
        String creator = "Nieznany";
        List<String> tags = new ArrayList<>();
        boolean isLog = true;
    }

    private ParsedHeaders parseFileHeaders(Path path) {
        ParsedHeaders headers = new ParsedHeaders();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < 15) {
                count++;
                String trimmed = line.trim();
                if (trimmed.startsWith("# Description:")) headers.description = trimmed.substring(14).trim();
                else if (trimmed.startsWith("# Creator:")) headers.creator = trimmed.substring(10).trim();
                else if (trimmed.startsWith("# Tags:")) {
                    String tagsRaw = trimmed.substring(7).trim();
                    if (!tagsRaw.isEmpty()) headers.tags = Arrays.asList(tagsRaw.split(",\\s*"));
                }
                else if (trimmed.startsWith("# IsLog:")) headers.isLog = Boolean.parseBoolean(trimmed.substring(8).trim());
            }
        } catch (IOException e) {
            System.err.println("[SYSTEM] Nie udało się odczytać nagłówków: " + path.getFileName());
        }
        return headers;
    }

    private LocalDateTime getFileUpdatedAt(Path path) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            return LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            return LocalDateTime.now();
        }
    }

    // --- GŁÓWNE METODY BIZNESOWE ---

    public List<PluginDTO> listLocalScripts() {
        List<ScheduledTask> allTasks = taskRepository.findAll();

        try (Stream<Path> paths = Files.list(Paths.get(scriptsPath))) {
            return paths.filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.toString().toLowerCase();
                        return name.endsWith(".py") || name.endsWith(".sh") || name.endsWith(".ps1");
                    })
                    .map(path -> {
                        String fileName = path.getFileName().toString();
                        Optional<ScheduledTask> taskOpt = allTasks.stream()
                                .filter(t -> t.getScriptName().equals(fileName))
                                .findFirst();
                        return mapLocalFileToDTO(path, taskOpt);
                    })
                    .toList();
        } catch (IOException e) {
            System.err.println("Błąd skanowania folderu scripts: " + e.getMessage());
            return List.of();
        }
    }

    public PluginDetailsDTO getPluginDetailsByFileName(String fileName) {
        Path path = Paths.get(scriptsPath).resolve(fileName).toAbsolutePath();
        if (!Files.exists(path)) throw new RuntimeException("Plik nie został znaleziony: " + fileName);

        try {
            String code = Files.readString(path);
            ParsedHeaders headers = parseFileHeaders(path);
            return new PluginDetailsDTO(headers.description, code);
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się odczytać zawartości: " + e.getMessage());
        }
    }

    public boolean isLogScript(String fileName) {
        Path path = Paths.get(scriptsPath).resolve(fileName);
        if (!Files.exists(path)) {
            throw new RuntimeException("Nie można utworzyć zadania. Plik nie istnieje: " + fileName);
        }
        return parseFileHeaders(path).isLog;
    }

    public void savePluginToDisk(Plugin plugin) throws Exception {
        String ext = (plugin.getLanguage() != null && plugin.getLanguage().startsWith("."))
                ? plugin.getLanguage() : "." + plugin.getLanguage();
        String fileName = plugin.getName() + ext;
        Path directory = Paths.get(scriptsPath);
        Path filePath = directory.resolve(fileName);

        if (!Files.exists(directory)) Files.createDirectories(directory);

        StringBuilder fileContent = new StringBuilder();
        fileContent.append("# Creator: ").append(plugin.getCreator() != null ? plugin.getCreator() : "Unknown").append("\n");
        fileContent.append("# Description: ").append(plugin.getDescription() != null ? plugin.getDescription() : "Brak opisu").append("\n");
        if (plugin.getTags() != null && !plugin.getTags().isEmpty()) {
            fileContent.append("# Tags: ").append(String.join(", ", plugin.getTags())).append("\n");
        }
        fileContent.append("# IsLog: ").append(plugin.isLog()).append("\n\n");

        if (plugin.getCode() != null) {
            String unescaped = plugin.getCode()
                    .replace("\\n", "\n")
                    .replace("\\t", "\t")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
            fileContent.append(unescaped);
        }

        Files.writeString(filePath, fileContent.toString());
    }

    // --- MAPOWANIE DTO ---

    public PluginDTO mapStorePluginToDTO(Plugin p) {
        String ext = (p.getLanguage() != null && p.getLanguage().startsWith("."))
                ? p.getLanguage() : "." + p.getLanguage();
        boolean installed = Files.exists(Paths.get(scriptsPath).resolve(p.getName() + ext));

        return new PluginDTO(
                p.getId(), p.getName(), p.getCreator(), p.getDescription(),
                ext, 0, p.getTags(), null, null, false, p.isLog(), installed
        );
    }

    public PluginDTO mapLocalFileToDTO(Path path, Optional<ScheduledTask> taskOpt) {
        String fileName = path.getFileName().toString();
        int dotIdx = fileName.lastIndexOf('.');
        String name = (dotIdx == -1) ? fileName : fileName.substring(0, dotIdx);
        String ext = (dotIdx == -1) ? "" : fileName.substring(dotIdx);

        long weightKb = 0;
        try { weightKb = Files.size(path) / 1024; } catch (IOException ignored) {}

        ParsedHeaders headers = parseFileHeaders(path);

        return new PluginDTO(
                taskOpt.map(ScheduledTask::getId).orElse(null),
                name, headers.creator, headers.description, ext, weightKb, headers.tags,
                taskOpt.map(ScheduledTask::getCronExpression).orElse(null),
                getFileUpdatedAt(path),
                taskOpt.map(ScheduledTask::isActive).orElse(false),
                taskOpt.map(ScheduledTask::isLog).orElse(headers.isLog),
                true
        );
    }
}