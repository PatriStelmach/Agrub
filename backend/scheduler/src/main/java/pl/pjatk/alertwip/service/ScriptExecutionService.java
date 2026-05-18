package pl.pjatk.alertwip.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.config.SettingsSeeder;
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
import java.util.concurrent.TimeUnit;

@Service
public class ScriptExecutionService {

    private final GlobalProblemRepository problemRepository;
    private final TaskExecutionLogRepository logRepository;
    private final SseNotifService sseService;
    private final AlertRoutingService routingService;
    private final ActiveAlertCache activeAlertCache;
    private final SystemSettingService systemSettingService;

    @Value("${app.scripts.path}")
    private String scriptsPath;

    public record ScriptResult(int exitCode, String output) {}

    public ScriptExecutionService(TaskExecutionLogRepository logRepository,
                                  GlobalProblemRepository problemRepository,
                                  SseNotifService sseService,
                                  AlertRoutingService routingService,
                                  ActiveAlertCache activeAlertCache,
                                  SystemSettingService systemSettingService) {
        this.problemRepository = problemRepository;
        this.logRepository = logRepository;
        this.sseService = sseService;
        this.routingService = routingService;
        this.activeAlertCache = activeAlertCache;
        this.systemSettingService = systemSettingService;
    }

    // Jak odpalamy bez argumentów
    public ScriptResult runScript(ScheduledTask task) {
        return runScript(task, null);
    }

    // Jak odpalamy z argumentami
    public ScriptResult runScript(ScheduledTask task, String overrideArgs) {
        String scriptName = task.getScriptName();

        // Jeśli nie podaliśmy overrideArgs to bierzemy z bazy
        String rawArgs = (overrideArgs != null) ? overrideArgs : task.getArguments();

        // czyścimy argumenty i parsujemy
        String[] args = (rawArgs != null && !rawArgs.isBlank())
                ? rawArgs.trim().split("\\s+") : new String[0];


        StringBuilder outputCollector = new StringBuilder();
        String status = "SUCCESS";
        int exitCode = 0;

        try {
            Path fullScriptPath = Paths.get(scriptsPath).resolve(scriptName).toAbsolutePath();

            if (!Files.exists(fullScriptPath)) {
                throw new Exception("Plik nie istnieje w lokalizacji: " + fullScriptPath);
            }

            String extension = scriptName.substring(scriptName.lastIndexOf('.')).toLowerCase();
            String interpreter;
            switch (extension) {
                case ".py":
                    interpreter = "python";
                    break;

                case ".bash":
                case ".sh":
                    interpreter = "bash";
                    break;

                case ".psm1":
                case ".ps1":
                    //trzeba sprawdzić jako os jest
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        interpreter = "powershell";
                    }
                    else {
                        interpreter = "pwsh";
                    }
                    interpreter = "pwsh";
                    break;
                default:
                    throw new Exception("Nieobsługiwane rozszerzenie pliku: " + extension);
            }

            String[] command = new String[args.length + 2];
            command[0] = interpreter;
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

            //customowy timeout
            long timeoutSeconds = 30L; // Wartość domyślna
            try {
                String timeoutStr = systemSettingService.getValue("scripts_execution_timeout_seconds", "30");
                timeoutSeconds = Long.parseLong(timeoutStr);
            } catch (Exception e) {
                System.err.println("Nie udało się sparsować timeoutu z bazy, używam domyślnych 30s.");
            }

            boolean exited = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

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
            handleAlerting(task, exitCode, outputCollector.toString());
        }
        return new ScriptResult(exitCode, outputCollector.toString());
    }

    private void handleAlerting(ScheduledTask task, int exitCode, String output) {
        // Unikalny klucz dla skryptu to np. "[SCRIPT] 12"
        String uniqueKey = "[SCRIPT] " + task.getId();

        GlobalProblem cachedProblem = activeAlertCache.getByUniqueKey(uniqueKey);

        if (exitCode != 0) {
            if (task.getSeverity() >= 2) {
                String newMessage = output.length() > 255 ? output.substring(0, 252) + "..." : output;

                if (cachedProblem == null) {
                    // nowy błąd
                    GlobalProblem problem = new GlobalProblem();
                    problem.setUniqueKey(uniqueKey);
                    problem.setSubject(task.getTaskName());
                    problem.setSource("Local Script");
                    problem.setOriginType(task.getTaskName());
                    problem.setStatus("Sent");
                    problem.setCreatedAt(LocalDateTime.now());
                    problem.setMessage(newMessage);
                    problem.setSeverity(task.getSeverity());

                    routingService.processVisibility(problem);
                    GlobalProblem saved = problemRepository.save(problem);
                    activeAlertCache.updateAlert(saved);

                    sseService.sendAlert("NEW_ALERT", saved);
                } else {
                    // Błąd już jest, sprawdzamy czy coś się zmeiniło

                    boolean isMessageDifferent = !newMessage.equals(cachedProblem.getMessage());
                    boolean isSeverityDifferent = (cachedProblem.getSeverity() != task.getSeverity()) && !cachedProblem.isSeverityLocked();

                    // Aktualizujemy tylko jak coś się zmieni
                    if (isMessageDifferent || isSeverityDifferent) {

                        if (isMessageDifferent) {
                            cachedProblem.setMessage(newMessage);
                        }

                        if (isSeverityDifferent) {
                            cachedProblem.setSeverity(task.getSeverity());
                        }

                        routingService.processVisibility(cachedProblem);

                        // Zapisujemy zmiany do bazy i odświeżamy Cache
                        GlobalProblem saved = problemRepository.save(cachedProblem);
                        activeAlertCache.updateAlert(saved);

                        sseService.sendAlert("ALERT_UPDATE", saved);
                        System.out.println("[LOG] Zaktualizowano alert dla skryptu: " + task.getTaskName());
                    }
                }
            } else {
                System.out.println("[LOG] Skrypt " + task.getTaskName() + " zakończył się błędem, ale ma severity 1.");
            }
        } else {
            // Jest ok zamykamy problem
            if (cachedProblem != null) {
                cachedProblem.setStatus("Done");
                cachedProblem.setClosedAt(LocalDateTime.now());

                // Zapisujemy zamknięcie w bazie i automatycznie wyrzucamy z Cache
                problemRepository.save(cachedProblem);
                activeAlertCache.updateAlert(cachedProblem);
                sseService.sendAlert("ALERT_RESOLVED", cachedProblem);

                System.out.println("[PYTHON] Skrypt naprawiony. Alert zamknięty: " + task.getTaskName());
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