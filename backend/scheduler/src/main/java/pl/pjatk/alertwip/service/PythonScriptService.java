package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.ScheduledTask;
import pl.pjatk.alertwip.model.TaskExecutionLog;
import pl.pjatk.alertwip.repository.TaskExecutionLogRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class PythonScriptService {

    private final TaskExecutionLogRepository logRepository;

    public PythonScriptService(TaskExecutionLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void runScript(ScheduledTask task) {
        String scriptName = task.getScriptName();
        String[] args = task.getArguments() != null ? task.getArguments().split(" ") : new String[0];

        StringBuilder outputCollector = new StringBuilder();
        String status = "SUCCESS";

        try {
            // Przygotowanie komendy
            String[] command = new String[args.length + 2];
            command[0] = "python";
            command[1] = "../AlertWIP/scripts/" + scriptName;
            System.arraycopy(args, 0, command, 2, args.length);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Odczyt logów w czasie rzeczywistym i gromadzenie ich dla bazy danych
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[PYTHON][" + scriptName + "]: " + line);
                    outputCollector.append(line).append("\n");
                }
            }

            // Oczekiwanie na zakończenie z timeoutem
            boolean exited = process.waitFor(30, TimeUnit.SECONDS);
            if (!exited) {
                process.destroyForcibly();
                status = "TIMEOUT";
                outputCollector.append("\n--- TIMEOUT: Skrypt został ubity przez system ---");
            }

        } catch (Exception e) {
            status = "CRITICAL_ERROR";
            outputCollector.append("BŁĄD KRYTYCZNY: ").append(e.getMessage());
        } finally {
            // Zapis wyniku do bazy danych MySQL
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
            System.err.println("Nie udało się zapisać logu do bazy: " + e.getMessage());
        }
    }
}