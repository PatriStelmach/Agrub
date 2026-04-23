package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.config.DynamicSchedulerConfig;
import pl.pjatk.alertwip.dto.TaskRequestDTO;
import pl.pjatk.alertwip.model.ScheduledTask;
import pl.pjatk.alertwip.repository.ScheduledTaskRepository;
import pl.pjatk.alertwip.service.PluginManagerService;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*") // Zawsze warto pamiętać o CORS dla frontendu
public class TaskController {

    private final ScheduledTaskRepository repository;
    private final DynamicSchedulerConfig schedulerConfig;
    private final PluginManagerService pluginManagerService;

    // Wstrzykujemy PluginManagerService
    public TaskController(ScheduledTaskRepository repository,
                          DynamicSchedulerConfig schedulerConfig,
                          PluginManagerService pluginManagerService) {
        this.repository = repository;
        this.schedulerConfig = schedulerConfig;
        this.pluginManagerService = pluginManagerService;
    }

    @PostMapping("/add")
    public ScheduledTask addTask(@RequestBody TaskRequestDTO dto) {
        ScheduledTask task = new ScheduledTask();
        task.setTaskName(dto.taskName());
        task.setScriptName(dto.scriptName());

        // Magia dzieje się tutaj - serwer sam decyduje na podstawie pliku!
        boolean isLog = pluginManagerService.isLogScript(dto.scriptName());
        task.setLog(isLog);

        // Logika tłumaczenia interwału na Crona
        if (dto.seconds() != null) {
            task.setCronExpression("*/" + dto.seconds() + " * * * * *");
        } else {
            task.setCronExpression(dto.cronExpression());
        }

        // Domyślnie nowe zadanie jest od razu aktywne (możesz to zmienić wg uznania)
        task.setActive(true);

        ScheduledTask saved = repository.save(task);
        schedulerConfig.updateSchedule(saved);
        return saved;
    }

    @PutMapping("/edit")
    public ScheduledTask editTask(@RequestBody ScheduledTask task) {
        boolean realIsLog = pluginManagerService.isLogScript(task.getScriptName());
        task.setLog(realIsLog);

        ScheduledTask saved = repository.save(task);
        schedulerConfig.updateSchedule(saved);
        return saved;
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        repository.deleteById(id);
        schedulerConfig.stopTask(id);
    }

    @PatchMapping("/{id}/toggle")
    public void toggle(@PathVariable Long id) {
        ScheduledTask task = repository.findById(id).orElseThrow();
        task.setActive(!task.isActive());
        repository.save(task);

        schedulerConfig.updateSchedule(task);
    }
}