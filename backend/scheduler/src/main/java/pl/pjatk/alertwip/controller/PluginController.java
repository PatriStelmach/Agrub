package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.model.Plugin;
import pl.pjatk.alertwip.repository.PluginRepository;
import pl.pjatk.alertwip.service.PythonScriptService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plugins")
@CrossOrigin(origins = "*")
public class PluginController {

    private final PluginRepository pluginRepository;
    private final PythonScriptService pythonScriptService;

    public PluginController(PluginRepository pluginRepository, PythonScriptService pythonScriptService) {
        this.pluginRepository = pluginRepository;
        this.pythonScriptService = pythonScriptService;
    }


    // --- getAllMyPlugins ---
    @GetMapping("/my")
    public List<Plugin> getAllMyPlugins() {
        return pluginRepository.findAll();
    }

    // --- getPluginsFromLibrary ---
    @GetMapping("/library")
    public List<Plugin> getPluginsFromLibrary(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String creator,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "10") int page_size,
            @RequestParam int first_id) {

        return pluginRepository.findLibrary(name, creator, language, first_id, page_size);
    }

    // --- addCustomPlugin ---
    @PostMapping("/add")
    public Plugin addCustomPlugin(@RequestBody Plugin plugin) {
        return pluginRepository.save(plugin);
    }

    // --- editMyPlugin ---
    @PutMapping("/edit")
    public Plugin editMyPlugin(@RequestBody Plugin plugin) {
        // Spring JPA save() przy istniejącym ID wykona Update
        return pluginRepository.save(plugin);
    }

    // --- changePluginsStatus ---
    @PatchMapping("/change-status")
    public void changePluginsStatus(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("id");
        List<Plugin> plugins = pluginRepository.findAllById(ids);
        plugins.forEach(p -> p.setActive(!p.isActive()));
        pluginRepository.saveAll(plugins);
    }

    // --- deletePlugins ---
    @DeleteMapping("/delete")
    public void deletePlugins(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("id");
        pluginRepository.deleteAllByIdInBatch(ids);
    }

    // --- downloadPlugin ---
    @PostMapping("/download/{id}")
    public ResponseEntity<String> downloadPlugin(@PathVariable Long id) {
        try {
            Plugin plugin = pluginRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono pluginu o ID: " + id));

            // Wywołanie metody zapisu z PythonScriptService
            pythonScriptService.savePluginToDisk(plugin);

            return ResponseEntity.ok("Plugin '" + plugin.getName() + "' został pobrany do folderu lokalnego.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Błąd podczas pobierania pluginu: " + e.getMessage());
        }
    }
}