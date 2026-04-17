package pl.pjatk.alertwip.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.Plugin;
import pl.pjatk.alertwip.model.ScheduledTask;
import pl.pjatk.alertwip.model.TaskExecutionLog;
import pl.pjatk.alertwip.repository.TaskExecutionLogRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class PythonScriptService {

    private final TaskExecutionLogRepository logRepository;

    // Pobieramy ścieżkę z application.properties
    @Value("${app.scripts.path}")
    private String scriptsPath;

    public PythonScriptService(TaskExecutionLogRepository logRepository) {
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

        try {
            // Budujemy pełną ścieżkę do skryptu
            Path fullScriptPath = Paths.get(scriptsPath).resolve(scriptName).toAbsolutePath();

            if (!Files.exists(fullScriptPath)) {
                throw new Exception("Plik nie istnieje w lokalizacji: " + fullScriptPath);
            }

            // Przygotowanie komendy systemowej
            String[] command = new String[args.length + 2];
            command[0] = "python"; // Upewnij się, że 'python' jest w PATH (lub zmień na 'python3')
            command[1] = fullScriptPath.toString();
            System.arraycopy(args, 0, command, 2, args.length);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Odczyt wyjścia (Stream Pumping)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[PYTHON][" + scriptName + "]: " + line);
                    outputCollector.append(line).append("\n");
                }
            }

            // Limit czasu na wykonanie (30 sekund)
            boolean exited = process.waitFor(30, TimeUnit.SECONDS);
            if (!exited) {
                process.destroyForcibly();
                status = "TIMEOUT";
                outputCollector.append("\n--- BŁĄD: Skrypt przekroczył czas wykonania i został zatrzymany ---");
            } else if (process.exitValue() != 0) {
                status = "FAILED";
                outputCollector.append("\n--- Skrypt zakończony kodem błędu: ").append(process.exitValue()).append(" ---");
            }

        } catch (Exception e) {
            status = "CRITICAL_ERROR";
            outputCollector.append("BŁĄD SYSTEMOWY: ").append(e.getMessage());
        } finally {
            saveLogToDatabase(task, outputCollector.toString(), status);
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
}