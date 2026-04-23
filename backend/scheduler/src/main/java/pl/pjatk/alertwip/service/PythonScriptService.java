package pl.pjatk.alertwip.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.model.ScheduledTask;
import pl.pjatk.alertwip.model.TaskExecutionLog;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.repository.TaskExecutionLogRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PythonScriptService {

    private final GlobalProblemRepository problemRepository;
    private final TaskExecutionLogRepository logRepository;
    private final SseNotifService sseService;

    @Value("${app.scripts.path}")
    private String scriptsPath;

    public PythonScriptService(TaskExecutionLogRepository logRepository,
                               GlobalProblemRepository problemRepository,
                               SseNotifService sseService) {
        this.problemRepository = problemRepository;
        this.logRepository = logRepository;
        this.sseService = sseService;
    }

    /**
     * Uruchamia fizyczny plik skryptu.
     */
    public void runScript(ScheduledTask task) {
        String scriptName = task.getScriptName();
        String[] args = (task.getArguments() != null && !task.getArguments().isEmpty())
                ? task.getArguments().split(" ") : new String[0];

        StringBuilder outputCollector = new StringBuilder();
        String status = "SUCCESS";
        int exitCode = 0;

        try {
            Path fullScriptPath = Paths.get(scriptsPath).resolve(scriptName).toAbsolutePath();

            if (!Files.exists(fullScriptPath)) {
                throw new Exception("Plik nie istnieje w lokalizacji: " + fullScriptPath);
            }

            String[] command = new String[args.length + 2];
            command[0] = "python"; // Jeśli kiedyś dodasz wsparcie dla Bash, tutaj sprawdzisz rozszerzenie pliku
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
                exitCode = -1;
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
            exitCode = -2;
            outputCollector.append("BŁĄD SYSTEMOWY: ").append(e.getMessage());
        } finally {
            saveLogToDatabase(task, outputCollector.toString(), status);

            // Wywołujemy mechanizm alertowania unconditionally - wewnątrz on sam sprawdzi,
            // czy zignorować (severity 1), utworzyć (severity >= 2), czy rozwiązać stary alert
            handleAlerting(task, exitCode, outputCollector.toString());
        }
    }

    private void handleAlerting(ScheduledTask task, int exitCode, String output) {
        Optional<GlobalProblem> existingProblem = problemRepository.findByTaskId(task.getId());

        if (exitCode != 0) {
            // SCENARIUSZ: BŁĄD SKRYPTU
            if (task.getSeverity() >= 2) {
                // Severity 2-5 -> Traktujemy jako ERROR (GlobalProblem)
                GlobalProblem problem = existingProblem.orElse(new GlobalProblem());
                problem.setTaskId(task.getId());
                problem.setTaskName(task.getTaskName());
                problem.setLastErrorMessage(output.length() > 255 ? output.substring(0, 252) + "..." : output);
                problem.setSeverity(task.getSeverity()); // Używamy czystego int z modelu
                problem.setOccurrenceTime(LocalDateTime.now());

                GlobalProblem saved = problemRepository.save(problem);

                if (existingProblem.isEmpty()) {
                    sseService.sendAlert("NEW_ALERT", saved);
                }
            } else {
                // Severity 1 -> Traktujemy jako LOG
                // Na razie tutaj nic nie robimy (docelowo: zapis do ogólnego logu systemowego)
                System.out.println("[LOG] Skrypt " + task.getTaskName() + " zakończył się błędem, ale ma severity 1.");
            }
        } else {
            // SCENARIUSZ: SKRYPT OK
            if (existingProblem.isPresent()) {
                GlobalProblem problemToResolve = existingProblem.get();
                problemRepository.delete(problemToResolve);
                sseService.sendAlert("ALERT_RESOLVED", problemToResolve);
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
}