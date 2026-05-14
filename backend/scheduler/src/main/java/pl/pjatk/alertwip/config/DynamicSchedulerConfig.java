package pl.pjatk.alertwip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import pl.pjatk.alertwip.model.ScheduledTask;
import pl.pjatk.alertwip.repository.ScheduledTaskRepository;
import pl.pjatk.alertwip.service.ScriptExecutionService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Configuration
public class DynamicSchedulerConfig implements SchedulingConfigurer {

    private final ScheduledTaskRepository repository;
    private final ScriptExecutionService pythonService;
    private final TaskScheduler taskScheduler;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public DynamicSchedulerConfig(ScheduledTaskRepository repository,
                                  ScriptExecutionService pythonService,
                                  TaskScheduler taskScheduler) {
        this.repository = repository;
        this.pythonService = pythonService;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler);
        // Przy starcie aplikacji ładujemy wszystkie aktywne zadania raz
        loadAllActiveTasks();
    }

    private void loadAllActiveTasks() {
        repository.findByActiveTrue().forEach(this::updateSchedule);
    }

    /**
     * Główna metoda wywoływana przez kontroler po dodaniu lub edycji zadania.
     */
    public void updateSchedule(ScheduledTask task) {
        stopTask(task.getId());

        if (task.isActive() && task.getCronExpression() != null) {
            try {
                ScheduledFuture<?> future = taskScheduler.schedule(
                        () -> {
                            // Rozbudowany log startu
                            System.out.println("\n==================================================");
                            System.out.println("[CRON TRIGGER] Odpalam zadanie: " + task.getTaskName());
                            System.out.println("[CRON TRIGGER] Plik docelowy: " + task.getScriptName());
                            System.out.println("[CRON TRIGGER] Czas: " + java.time.LocalDateTime.now());

                            try {
                                // Właściwe wykonanie skryptu
                               int exitCode = pythonService.runScript(task).exitCode();
                               System.out.println("[CRON FINISHED] Zakończono zadanie: " + task.getTaskName() + " | Exit code: " + exitCode);
                            } catch (Exception e) {
                                System.err.println("[CRON ERROR] Błąd w trakcie wykonywania " + task.getScriptName() + ": " + e.getMessage());
                            }

                            // Log zakończenia

                            System.out.println("==================================================\n");
                        },
                        new CronTrigger(task.getCronExpression())
                );
                scheduledTasks.put(task.getId(), future);
                System.out.println("[SCHEDULER] Zaplanowano/Zaktualizowano: " + task.getTaskName() + " z Cronem: " + task.getCronExpression());
            } catch (Exception e) {
                System.err.println("[SCHEDULER] Błąd CRON w zadaniu " + task.getId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Metoda wywoływana przy usuwaniu zadania lub wyłączaniu go.
     */
    public void stopTask(Long taskId) {
        ScheduledFuture<?> future = scheduledTasks.get(taskId);
        if (future != null) {
            future.cancel(false);
            scheduledTasks.remove(taskId);
            System.out.println("[SCHEDULER] Zatrzymano zadanie ID: " + taskId);
        }
    }
}