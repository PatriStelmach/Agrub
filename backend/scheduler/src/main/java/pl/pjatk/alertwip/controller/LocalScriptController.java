package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.PluginDTO;
import pl.pjatk.alertwip.dto.PluginDetailsDTO;
import pl.pjatk.alertwip.dto.PluginSaveDTO;
import pl.pjatk.alertwip.model.ScheduledTask;
import pl.pjatk.alertwip.repository.ScheduledTaskRepository;
import pl.pjatk.alertwip.service.PluginManagerService;
import pl.pjatk.alertwip.service.ScriptExecutionService;

import java.util.List;

@RestController
@RequestMapping("/api/local-scripts")
public class LocalScriptController {

    private final PluginManagerService pluginManagerService;
    private final ScheduledTaskRepository taskRepository;
    private final ScriptExecutionService scriptExecutionService;

    public LocalScriptController(PluginManagerService pluginManagerService,
                                 ScheduledTaskRepository taskRepository,
                                 ScriptExecutionService scriptExecutionService) {
        this.pluginManagerService = pluginManagerService;
        this.taskRepository = taskRepository;
        this.scriptExecutionService = scriptExecutionService;
    }

    // Lista wszystkich plików na dysku + dane z ScheduledTask (jeśli istnieją)
    @GetMapping("/list")
    public List<PluginDTO> getMyLocalScripts() {
        return pluginManagerService.listLocalScripts();
    }

    // Podgląd kodu i opisu na podstawie nazwy pliku (nie ID!)
    @GetMapping("/{fileName:.+}/details")
    public PluginDetailsDTO getDetails(@PathVariable String fileName) {
        return pluginManagerService.getPluginDetailsByFileName(fileName);
    }

    @PostMapping("/{fileName:.+}/run")
    public ResponseEntity<String> runScriptByFileName(@PathVariable String fileName) {
        try {
            // Wywołujemy jedną spójną metodę z Menedżera Pluginów
            ScriptExecutionService.ScriptResult result = pluginManagerService.runManualScript(fileName);
            return ResponseEntity.ok(result.output());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Błąd wykonania: " + e.getMessage());
        }
    }


    @PutMapping("/{fileName:.+}/edit")
    public ResponseEntity<String> editScript(
            @PathVariable String fileName,
            @RequestBody PluginSaveDTO partialDto) {
        try {
            // Przekazujemy wszystko do serwisu
            pluginManagerService.updatePluginPartial(fileName, partialDto);
            return ResponseEntity.ok("Skrypt " + fileName + " został zaktualizowany pomyślnie.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Błąd podczas aktualizacji: " + e.getMessage());
        }
    }

    @PostMapping("/save-all")
    public ResponseEntity<String> saveAll(@RequestBody PluginSaveDTO dto) {
        try {
            pluginManagerService.saveFullConfig(dto);
            return ResponseEntity.ok("Plugin i harmonogram zostały zaktualizowane pomyślnie.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Błąd podczas zapisu: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePlugins(@RequestBody List<String> fileNames) {
        try {
            pluginManagerService.deletePlugins(fileNames);
            return ResponseEntity.ok("Usunięto pomyślnie " + fileNames.size() + " pluginów.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Błąd podczas usuwania: " + e.getMessage());
        }
    }

    @PostMapping("/change-status")
    public ResponseEntity<String> toggleStatusBulk(@RequestBody List<String> fileNames) {
        try {
            pluginManagerService.togglePlugins(fileNames);
            return ResponseEntity.ok("Zmieniono status dla " + fileNames.size() + " pluginów.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Nie można zmienić statusu: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("błąd serwera: " + e.getMessage());
        }
    }
}