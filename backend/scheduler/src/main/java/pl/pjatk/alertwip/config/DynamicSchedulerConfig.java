package pl.pjatk.alertwip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pl.pjatk.alertwip.model.ScheduledTask;
import pl.pjatk.alertwip.repository.ScheduledTaskRepository;
import pl.pjatk.alertwip.service.PythonScriptService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Configuration
public class DynamicSchedulerConfig implements SchedulingConfigurer {

    private final ScheduledTaskRepository repository;
    private final PythonScriptService pythonService;
    private final TaskScheduler taskScheduler;

    // Mapa przechowująca zaplanowane zadania, aby móc je anulować/aktualizować
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    public DynamicSchedulerConfig(ScheduledTaskRepository repository,
                                  PythonScriptService pythonService,
                                  TaskScheduler taskScheduler) {
        this.repository = repository;
        this.pythonService = pythonService;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // Ustawiamy egzekutor dla zadań planowanych
        taskRegistrar.setScheduler(taskScheduler);

        // Uruchamiamy proces odświeżania zadań z bazy danych
        refreshSchedule();
    }

    // Metoda wywoływana cyklicznie (np. co 60 sekund), aby sprawdzić zmiany w bazie
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
    public void refreshSchedule() {
        var tasksFromDb = repository.findByActiveTrue();

        // Usuwamy zadania, które zostały wyłączone w bazie
        scheduledTasks.keySet().removeIf(taskId -> {
            boolean stillActive = tasksFromDb.stream().anyMatch(t -> t.getId().equals(taskId));
            if (!stillActive) {
                scheduledTasks.get(taskId).cancel(false);
                System.out.println("Zadanie ID: " + taskId + " zostało usunięte z harmonogramu.");
                return true;
            }
            return false;
        });

        // Dodajemy nowe zadania lub aktualizujemy istniejące
        for (ScheduledTask task : tasksFromDb) {
            if (!scheduledTasks.containsKey(task.getId())) {
                scheduleTask(task);
            }
        }
    }

    private void scheduleTask(ScheduledTask task) {
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> pythonService.runScript(task),
                new org.springframework.scheduling.support.CronTrigger(task.getCronExpression())
        );

        scheduledTasks.put(task.getId(), future);
        System.out.println("Zaplanowano zadanie: " + task.getTaskName() + " (Cron: " + task.getCronExpression() + ")");
    }
}