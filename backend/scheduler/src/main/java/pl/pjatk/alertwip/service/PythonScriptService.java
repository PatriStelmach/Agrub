package pl.pjatk.alertwip.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.*;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.repository.TaskExecutionLogRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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

    // Pobieramy ścieżkę z application.properties
    @Value("${app.scripts.path}")
    private String scriptsPath;

    public PythonScriptService(TaskExecutionLogRepository logRepository,
                               GlobalProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
        this.logRepository = logRepository;
    }

    /**
     * Zapisuje kod pluginu z bazy danych do fizycznego pliku na dysku.
     * Używa ścieżki zdefiniowanej w app.scripts.path.
     */
    public void savePluginToDisk(Plugin plugin) throws Exception {
        // Ustalenie rozszerzenia na podstawie pola language (domyślnie .py)
        String extension = (plugin.getLanguage() != null) ? "." + plugin.getLanguage() : ".py";
        String fileName = plugin.getName() + extension;

        // Używamy Path.of() dla poprawnej obsługi separatorów systemowych (Windows/Linux)
        Path directory = Paths.get(scriptsPath);
        Path filePath = directory.resolve(fileName);

        // Automatyczne tworzenie folderu, jeśli go nie ma
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        // Zapis/Nadpisanie pliku
        Files.writeString(filePath, plugin.getCode());
        System.out.println("[SYSTEM] Plugin '" + fileName + "' został zapisany w: " + filePath.toAbsolutePath());
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

    public List<LocalScriptDTO> listLocalScripts() {
        List<LocalScriptDTO> scripts = new ArrayList<>();
        try (Stream<Path> paths = Files.list(Paths.get(scriptsPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".py"))
                    .forEach(path -> scripts.add(parseMetadata(path)));
        } catch (IOException e) {
            System.err.println("Błąd skanowania folderu scripts: " + e.getMessage());
        }
        return scripts;
    }

    private LocalScriptDTO parseMetadata(Path path) {
        String fileName = path.getFileName().toString();
        String description = "Brak opisu w pliku .py";
        List<String> tags = new ArrayList<>();
        boolean isLog = true;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int linesRead = 0;
            // Czytamy tylko pierwsze 10 linii, żeby nie marnować zasobów
            while ((line = reader.readLine()) != null && linesRead < 10) {
                linesRead++;
                if (line.startsWith("# Description:")) {
                    description = line.replace("# Description:", "").trim();
                } else if (line.startsWith("# Tags:")) {
                    String tagsRaw = line.replace("# Tags:", "").trim();
                    tags = Arrays.asList(tagsRaw.split(",\\s*"));
                } else if (line.startsWith("# IsLog:")) {
                    isLog = Boolean.parseBoolean(line.replace("# IsLog:", "").trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Nie można odczytać pliku: " + fileName);
        }

        return new LocalScriptDTO(fileName, description, tags, isLog);
    }

}