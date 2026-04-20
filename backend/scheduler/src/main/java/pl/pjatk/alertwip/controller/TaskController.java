package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.config.DynamicSchedulerConfig;
import pl.pjatk.alertwip.dto.TaskRequestDTO;
import pl.pjatk.alertwip.model.Plugin;
import pl.pjatk.alertwip.model.ScheduledTask;
import pl.pjatk.alertwip.repository.PluginRepository;
import pl.pjatk.alertwip.repository.ScheduledTaskRepository;
import pl.pjatk.alertwip.service.PythonScriptService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final ScheduledTaskRepository repository;
    private final DynamicSchedulerConfig schedulerConfig;

    public TaskController(ScheduledTaskRepository repository, DynamicSchedulerConfig schedulerConfig) {
        this.repository = repository;
        this.schedulerConfig = schedulerConfig;
    }

    @PostMapping("/add")
    public ScheduledTask addTask(@RequestBody TaskRequestDTO dto) {
        ScheduledTask task = new ScheduledTask();
        task.setTaskName(dto.taskName());
        task.setScriptName(dto.scriptName());
        task.setLog(dto.isLog());

        // Logika tłumaczenia
        if (dto.seconds() != null) {
            task.setCronExpression("*/" + dto.seconds() + " * * * * *");
        } else {
            task.setCronExpression(dto.cronExpression());
        }

        ScheduledTask saved = repository.save(task);
        schedulerConfig.updateSchedule(saved);
        return saved;
    }

    @PutMapping("/edit")
    public ScheduledTask editTask(@RequestBody ScheduledTask task) {
        ScheduledTask saved = repository.save(task);
        // REAKCJA NATYCHMIASTOWA
        schedulerConfig.updateSchedule(saved);
        return saved;
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        repository.deleteById(id);
        // ZATRZYMANIE W PAMIĘCI
        schedulerConfig.stopTask(id);
    }

    @PatchMapping("/{id}/toggle")
    public void toggle(@PathVariable Long id) {
        ScheduledTask task = repository.findById(id).orElseThrow();
        task.setActive(!task.isActive());
        repository.save(task);

        // Aktualizujemy harmonogram na podstawie nowego statusu active
        schedulerConfig.updateSchedule(task);
    }
}