package pl.pjatk.alertwip.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.alertwip.config.DynamicSchedulerConfig;
import pl.pjatk.alertwip.dto.PluginDTO;
import pl.pjatk.alertwip.dto.PluginDetailsDTO;
import pl.pjatk.alertwip.dto.PluginSaveDTO;
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
    private final DynamicSchedulerConfig dynamicSchedulerConfig;

    @Value("${app.scripts.path}")
    private String scriptsPath;

    public PluginManagerService(ScheduledTaskRepository taskRepository,
                                DynamicSchedulerConfig dynamicSchedulerConfig) {
        this.taskRepository = taskRepository;
        this.dynamicSchedulerConfig = dynamicSchedulerConfig;
    }

    // --- KLASY I METODY POMOCNICZE DO PLIKÓW ---
    private static class ParsedHeaders {
        String description = "Brak opisu w pliku";
        String creator = "Nieznany";
        List<String> tags = new ArrayList<>();
        int severity = 1; // Domyślnie traktujemy jako Log
    }

    private ParsedHeaders parseFileHeaders(Path path) {
        ParsedHeaders headers = new ParsedHeaders();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < 15) {
                count++;
                String trimmed = line.trim();
                if (trimmed.startsWith("# Description:")) {
                    headers.description = trimmed.substring(14).trim();
                } else if (trimmed.startsWith("# Creator:")) {
                    headers.creator = trimmed.substring(10).trim();
                } else if (trimmed.startsWith("# Tags:")) {
                    String tagsRaw = trimmed.substring(7).trim();
                    if (!tagsRaw.isEmpty()) headers.tags = Arrays.asList(tagsRaw.split(",\\s*"));
                } else if (trimmed.toLowerCase().startsWith("# severity:")) {
                    try {
                        headers.severity = Integer.parseInt(trimmed.substring(11).trim());
                    } catch (NumberFormatException e) {
                        headers.severity = 1; // Bezpieczny domyślny poziom w razie błędu parsowania
                    }
                }
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
        if (!Files.exists(path)) throw new RuntimeException("Plik nie istnieje: " + fileName);

        try {
            List<String> lines = Files.readAllLines(path);
            ParsedHeaders headers = parseFileHeaders(path);

            List<String> cleanCodeLines = new ArrayList<>();
            boolean passedSystemHeaders = false;

            for (String line : lines) {
                if (!passedSystemHeaders) {
                    String trimmed = line.trim();

                    // Sprawdzamy, czy to jest jeden z NASZYCH nagłówków (ignorując wielkość liter dla severity)
                    boolean isSystemHeader = trimmed.startsWith("# Creator:") ||
                            trimmed.startsWith("# Description:") ||
                            trimmed.startsWith("# Tags:") ||
                            trimmed.toLowerCase().startsWith("# severity:");

                    if (isSystemHeader) {
                        continue;
                    } else if (trimmed.isEmpty() && cleanCodeLines.isEmpty()) {
                        // Ignorujemy puste linie zaraz po nagłówkach
                        continue;
                    } else {
                        passedSystemHeaders = true;
                        cleanCodeLines.add(line);
                    }
                } else {
                    cleanCodeLines.add(line);
                }
            }

            return new PluginDetailsDTO(headers.description, String.join("\n", cleanCodeLines));
        } catch (IOException e) {
            throw new RuntimeException("Błąd odczytu pliku: " + e.getMessage());
        }
    }

    @Transactional
    public void saveFullConfig(PluginSaveDTO dto) throws Exception {
        String oldFileName = dto.oldName() + dto.extension();
        String newFileName = dto.name() + dto.extension();
        Path oldPath = Paths.get(scriptsPath).resolve(oldFileName);
        Path newPath = Paths.get(scriptsPath).resolve(newFileName);

        // 1. Obsługa zmiany nazwy pliku (Rename)
        if (!oldFileName.equals(newFileName) && Files.exists(oldPath)) {
            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 2. Budowanie nowej zawartości pliku (Nagłówki + Czysty Kod)
        StringBuilder content = new StringBuilder();
        content.append("# Creator: System\n");
        content.append("# Description: ").append(dto.description()).append("\n");
        content.append("# Tags: ").append(String.join(", ", dto.tags())).append("\n");
        content.append("# Severity: ").append(dto.severity()).append("\n\n");
        content.append(dto.code());

        Files.writeString(newPath, content.toString());

        // 3. Aktualizacja bazy danych (ScheduledTask)
        ScheduledTask task = taskRepository.findByScriptName(oldFileName)
                .orElse(new ScheduledTask());

        task.setTaskName(dto.name());
        task.setScriptName(newFileName);
        task.setCronExpression(dto.cronExpression());
        task.setSeverity(dto.severity());
        task.setActive(dto.active());

        ScheduledTask savedTask = taskRepository.save(task);

        // 4. Odświeżenie Schedulera
        dynamicSchedulerConfig.updateSchedule(savedTask);

        // Jeśli nazwa się zmieniła, musimy zatrzymać stary proces w schedulerze
        if (!oldFileName.equals(newFileName)) {
            dynamicSchedulerConfig.stopTask(task.getId());
        }
    }

    public int getScriptSeverity(String fileName) {
        Path path = Paths.get(scriptsPath).resolve(fileName);
        if (!Files.exists(path)) {
            throw new RuntimeException("Nie można odczytać zadania. Plik nie istnieje: " + fileName);
        }
        return parseFileHeaders(path).severity;
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
        fileContent.append("# Severity: ").append(plugin.getSeverity()).append("\n\n");

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

    @Transactional
    public void deletePlugins(List<String> fileNames) {
        for (String fileName : fileNames) {
            deletePluginByFileName(fileName);
        }
    }

    @Transactional
    public void deletePluginByFileName(String fileName) {
        Path path = Paths.get(scriptsPath).resolve(fileName).toAbsolutePath();
        Optional<ScheduledTask> taskOpt = taskRepository.findByScriptName(fileName);
        if (taskOpt.isPresent()) {
            ScheduledTask task = taskOpt.get();
            dynamicSchedulerConfig.stopTask(task.getId());
            taskRepository.delete(task);
        }
        try {
            if (Files.exists(path)) {
                Files.delete(path);
            } else {
                System.out.println("Plik nie istniał na dysku, usunięto tylko wpis z bazy: " + fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się usunąć pliku z dysku: " + e.getMessage());
        }
    }

    @Transactional
    public void togglePlugins(List<String> fileNames) {
        for (String fileName : fileNames) {
            togglePluginStatus(fileName);
        }
    }

    @Transactional
    public boolean togglePluginStatus(String fileName) {
        ScheduledTask task = taskRepository.findByScriptName(fileName)
                .orElseThrow(() -> new RuntimeException("Zadanie dla pliku " + fileName + " nie jest jeszcze skonfigurowane w bazie. Zapisz je najpierw."));
        task.setActive(!task.isActive());
        ScheduledTask savedTask = taskRepository.save(task);
        dynamicSchedulerConfig.updateSchedule(savedTask);
        return savedTask.isActive();
    }

    // --- MAPOWANIE DTO ---

    public PluginDTO mapStorePluginToDTO(Plugin p) {
        String ext = (p.getLanguage() != null && p.getLanguage().startsWith("."))
                ? p.getLanguage() : "." + p.getLanguage();
        boolean installed = Files.exists(Paths.get(scriptsPath).resolve(p.getName() + ext));

        return new PluginDTO(
                p.getId(), p.getName(), p.getCreator(), p.getDescription(),
                ext, 0, p.getTags(), null, null, false, p.getSeverity(), installed
        );
    }

    public PluginDTO mapLocalFileToDTO(Path path, Optional<ScheduledTask> taskOpt) {
        String fileName = path.getFileName().toString();
        int dotIdx = fileName.lastIndexOf('.');
        String name = (dotIdx == -1) ? fileName : fileName.substring(0, dotIdx);
        String ext = (dotIdx == -1) ? "" : fileName.substring(dotIdx);

        long weightKb = 0;
        try {
            weightKb = Files.size(path) / 1024;
        } catch (IOException ignored) {
        }

        ParsedHeaders headers = parseFileHeaders(path);

        return new PluginDTO(
                taskOpt.map(ScheduledTask::getId).orElse(null),
                name, headers.creator, headers.description, ext, weightKb, headers.tags,
                taskOpt.map(ScheduledTask::getCronExpression).orElse(null),
                getFileUpdatedAt(path),
                taskOpt.map(ScheduledTask::isActive).orElse(false),
                taskOpt.map(ScheduledTask::getSeverity).orElse(headers.severity),
                true
        );
    }
}