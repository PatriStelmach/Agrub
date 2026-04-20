package pl.pjatk.alertwip.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.dto.PluginDTO;
import pl.pjatk.alertwip.dto.PluginDetailsDTO;
import pl.pjatk.alertwip.model.*;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.repository.ScheduledTaskRepository;
import pl.pjatk.alertwip.repository.TaskExecutionLogRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PythonScriptService {

    private final GlobalProblemRepository problemRepository;
    private final TaskExecutionLogRepository logRepository;
    private final ScheduledTaskRepository taskRepository;

    // Pobieramy ścieżkę z application.properties
    @Value("${app.scripts.path}")
    private String scriptsPath;

    public PythonScriptService(TaskExecutionLogRepository logRepository,
                               GlobalProblemRepository problemRepository,
                               ScheduledTaskRepository taskRepository) {
        this.problemRepository = problemRepository;
        this.logRepository = logRepository;
        this.taskRepository = taskRepository;
    }

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
            int linesRead = 0;
            // Sprawdzamy tylko pierwsze 15 linii
            while ((line = reader.readLine()) != null && linesRead < 15) {
                linesRead++;
                String trimmed = line.trim();

                if (trimmed.startsWith("# Description:")) {
                    headers.description = trimmed.substring(14).trim();
                } else if (trimmed.startsWith("# Creator:")) {
                    headers.creator = trimmed.substring(10).trim();
                } else if (trimmed.startsWith("# Tags:")) {
                    String tagsRaw = trimmed.substring(7).trim();
                    if (!tagsRaw.isEmpty()) {
                        headers.tags = Arrays.asList(tagsRaw.split(",\\s*"));
                    }
                } else if (trimmed.startsWith("# IsLog:")) {
                    headers.isLog = Boolean.parseBoolean(trimmed.substring(8).trim());
                }
            }
        } catch (IOException e) {
            System.err.println("[SYSTEM] Nie udało się odczytać nagłówków z pliku: " + path.getFileName());
        }
        return headers;
    }

    /**
     * Zapisuje kod pluginu z bazy danych do fizycznego pliku na dysku.
     * Używa ścieżki zdefiniowanej w app.scripts.path.
     */
    public void savePluginToDisk(Plugin plugin) throws Exception {
        // 1. Przygotowanie nazwy pliku
        String extension = (plugin.getLanguage() != null) ? "." + plugin.getLanguage() : ".py";
        String fileName = plugin.getName() + extension;
        Path directory = Paths.get(scriptsPath);
        Path filePath = directory.resolve(fileName);

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        // 2. Budowanie nagłówka metadanych (Fix dla błędu nr 2)
        // To sprawi, że po pobraniu plik będzie miał zaszyte informacje o twórcy, opisie itp.
        StringBuilder fileContent = new StringBuilder();
        fileContent.append("# Creator: ").append(plugin.getCreator() != null ? plugin.getCreator() : "Unknown").append("\n");
        fileContent.append("# Description: ").append(plugin.getDescription() != null ? plugin.getDescription() : "No description").append("\n");

        // Tagi zapisujemy jako listę po przecinku
        if (plugin.getTags() != null && !plugin.getTags().isEmpty()) {
            fileContent.append("# Tags: ").append(String.join(", ", plugin.getTags())).append("\n");
        }

        fileContent.append("# IsLog: ").append(plugin.isLog()).append("\n");
        fileContent.append("\n"); // Pusta linia oddzielająca metadane od kodu

        // 3. Naprawa kodu (Fix dla błędu nr 1 - Unescaping)
        String rawCode = plugin.getCode();
        if (rawCode != null) {
            // Usuwamy escape'y dodane przez JSON (standardowe dla Java/Spring)
            String unescapedCode = rawCode
                    .replace("\\n", "\n")
                    .replace("\\t", "\t")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");

            fileContent.append(unescapedCode);
        }

        // 4. Zapis gotowego pliku
        Files.writeString(filePath, fileContent.toString());
        System.out.println("[SYSTEM] Plugin '" + fileName + "' zainstalowany poprawnie z metadanymi.");
    }

    /**
     * Uruchamia fizyczny plik skryptu znajdujący się w folderze skryptów.
     */
    public void runScript(ScheduledTask task) {
        String scriptName = task.getScriptName();
        String[] args = (task.getArguments() != null && !task.getArguments().isEmpty())
                ? task.getArguments().split(" ")
                : new String[0];

        StringBuilder outputCollector = new StringBuilder();
        String status = "SUCCESS";
        int exitCode = 0;

        try {
            Path fullScriptPath = Paths.get(scriptsPath).resolve(scriptName).toAbsolutePath();

            if (!Files.exists(fullScriptPath)) {
                throw new Exception("Plik nie istnieje w lokalizacji: " + fullScriptPath);
            }

            String[] command = new String[args.length + 2];
            command[0] = "python";
            command[1] = fullScriptPath.toString();
            System.arraycopy(args, 0, command, 2, args.length);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[PYTHON][" + scriptName + "]: " + line);
                    outputCollector.append(line).append("\n");
                }
            }

            boolean exited = process.waitFor(30, TimeUnit.SECONDS);

            if (!exited) {
                process.destroyForcibly();
                status = "TIMEOUT";
                exitCode = -1; // Traktujemy timeout jako błąd
                outputCollector.append("\n--- BŁĄD: Skrypt przekroczył czas wykonania i został zatrzymany ---");
            } else {
                exitCode = process.exitValue();
                if (exitCode != 0) {
                    status = "FAILED";
                    outputCollector.append("\n--- Skrypt zakończony kodem błędu: ").append(exitCode).append(" ---");
                }
            }

        } catch (Exception e) {
            status = "CRITICAL_ERROR";
            exitCode = -2; // Błędy systemowe również traktujemy jako awarię alertu
            outputCollector.append("BŁĄD SYSTEMOWY: ").append(e.getMessage());
        } finally {
            // Zapis do bazy logów (zawsze)
            saveLogToDatabase(task, outputCollector.toString(), status);

            // Logika Globalnych Problemów (tylko jeśli to ALERT, czyli isLog == false)
            if (!task.isLog()) {
                handleAlerting(task, exitCode, outputCollector.toString());
            }
        }
    }

    /**
     * Zarządza wpisami w tabeli GlobalProblem na podstawie wyniku wykonania skryptu.
     */
    private void handleAlerting(ScheduledTask task, int exitCode, String output) {
        // Szukamy istniejącego problemu dla tego konkretnego zadania
        Optional<GlobalProblem> existingProblem = problemRepository.findByTaskId(task.getId());

        if (exitCode != 0) {
            // Skrypt zgłosił błąd lub wybuchł błąd systemowy
            GlobalProblem problem = existingProblem.orElse(new GlobalProblem());

            problem.setTaskId(task.getId());
            problem.setTaskName(task.getTaskName());
            // Zapisujemy fragment outputu jako opis problemu, żeby od razu było widać co się stało
            problem.setLastErrorMessage(output.length() > 255 ? output.substring(0, 252) + "..." : output);
            problem.setOccurrenceTime(LocalDateTime.now());

            problemRepository.save(problem);

            if (existingProblem.isEmpty()) {
                System.out.println("[!!!] NOWY ALERT: " + task.getTaskName());
            }
        } else {
            // Skrypt zwrócił 0 (Sukces) - jeśli problem wisiał w bazie, usuwamy go
            if (existingProblem.isPresent()) {
                problemRepository.delete(existingProblem.get());
                System.out.println("[OK] RECOVERY: Problem rozwiązany dla: " + task.getTaskName());
            }
        }
    }

    private void saveLogToDatabase(ScheduledTask task, String output, String status) {
        try {
            TaskExecutionLog log = new TaskExecutionLog();
            log.setTask(task);
            log.setExecutionTime(LocalDateTime.now());
            log.setOutput(output);
            log.setStatus(status);
            logRepository.save(log);
        } catch (Exception e) {
            System.err.println("Błąd zapisu logów do MySQL: " + e.getMessage());
        }
    }

    public List<PluginDTO> listLocalScripts() {
        // 1. Pobieramy wszystkie zadania z bazy raz, aby uniknąć setek zapytań w pętli
        List<ScheduledTask> allTasks = taskRepository.findAll();

        try (Stream<Path> paths = Files.list(Paths.get(scriptsPath))) {
            return paths.filter(Files::isRegularFile)
                    // 2. Filtrujemy obsługiwane rozszerzenia
                    .filter(path -> {
                        String name = path.toString().toLowerCase();
                        return name.endsWith(".py") || name.endsWith(".sh") || name.endsWith(".ps1");
                    })
                    // 3. Mapujemy każdy plik na PluginDTO
                    .map(path -> {
                        String fileName = path.getFileName().toString();

                        // Sprawdzamy, czy ten konkretny plik ma wpis w ScheduledTask
                        Optional<ScheduledTask> taskOpt = allTasks.stream()
                                .filter(t -> t.getScriptName().equals(fileName))
                                .findFirst();

                        // Wywołujemy zunifikowaną metodę mapującą, którą stworzyliśmy wcześniej
                        return mapLocalFileToDTO(path, taskOpt);
                    })
                    .toList(); // W nowszych Javach można użyć .toList() zamiast .collect(Collectors.toList())
        } catch (IOException e) {
            System.err.println("Błąd skanowania folderu scripts: " + e.getMessage());
            return List.of();
        }
    }

    public PluginDetailsDTO getPluginDetailsByFileName(String fileName) {
        Path path = Paths.get(scriptsPath).resolve(fileName).toAbsolutePath();

        if (!Files.exists(path)) {
            throw new RuntimeException("Plik skryptu nie został znaleziony: " + fileName);
        }

        try {
            String code = Files.readString(path);

            ParsedHeaders headers = parseFileHeaders(path);

            return new PluginDetailsDTO(headers.description, code);

        } catch (IOException e) {
            System.err.println("[SYSTEM] Błąd podczas odczytu pliku: " + fileName);
            throw new RuntimeException("Nie udało się odczytać zawartości pliku: " + e.getMessage());
        }
    }

    public PluginDTO mapStorePluginToDTO(Plugin p) {
        // Sprawdzamy czy użytkownik już to pobrał (czy plik o takiej nazwie istnieje)
        String expectedFileName = p.getName() + (p.getLanguage().equalsIgnoreCase("py") ? ".py" : ".sh");
        boolean installed = Files.exists(Paths.get(scriptsPath).resolve(expectedFileName));

        return new PluginDTO(
                p.getId(),
                null, // To jest w chmurze/sklepie, nie ma jeszcze nazwy pliku lokalnego
                p.getName(),
                p.getCreator(),
                p.getDescription(), // Upewnij się, że encja Plugin ma to pole!
                p.getLanguage(),
                0, // Sklep nie musi podawać wagi przed pobraniem (lub oblicz z p.getCode().length())
                p.getTags(),
                null, // Sklep nie ma harmonogramu
                null, // Sklep nie ma daty edycji pliku
                false, // Nieaktywne w sklepie
                p.isLog(),
                installed
        );
    }

    public PluginDTO mapLocalFileToDTO(Path path, Optional<ScheduledTask> taskOpt) {
        String fileName = path.getFileName().toString();
        ParsedHeaders headers = parseFileHeaders(path); // Wykorzystujemy naszą nową metodę

        long weight = 0;
        LocalDateTime updatedAt = LocalDateTime.now();
        try {
            weight = Files.size(path) / 1024;
            updatedAt = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault());
        } catch (IOException ignored) {}

        String language = fileName.endsWith(".py") ? "python" :
                fileName.endsWith(".sh") ? "bash" : "PowerShell";

        return new PluginDTO(
                taskOpt.map(ScheduledTask::getId).orElse(null),
                fileName,
                taskOpt.map(ScheduledTask::getTaskName).orElse(fileName),
                headers.creator,
                headers.description,
                language,
                weight,
                headers.tags,
                taskOpt.map(ScheduledTask::getCronExpression).orElse(null),
                updatedAt,
                taskOpt.map(ScheduledTask::isActive).orElse(false),
                taskOpt.map(ScheduledTask::isLog).orElse(headers.isLog),
                true // To jest lokalne, więc zainstalowane
        );
    }

}