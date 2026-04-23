package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.PluginDTO;
import pl.pjatk.alertwip.model.Plugin;
import pl.pjatk.alertwip.repository.PluginRepository;
import pl.pjatk.alertwip.service.PluginManagerService;
import pl.pjatk.alertwip.service.PythonScriptService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/plugins")
@CrossOrigin(origins = "*")
public class PluginController {

    private final PluginRepository pluginRepository;
    private final PluginManagerService pluginManagerService;

    public PluginController(PluginRepository pluginRepository, PluginManagerService pluginManagerService) {
        this.pluginRepository = pluginRepository;
        this.pluginManagerService = pluginManagerService;
    }

    // Pobiera wszystkie pluginy ze "sklepu" i mapuje na DTO
    @GetMapping("/all")
    public List<PluginDTO> getAllMyPlugins() {
        return pluginRepository.findAll().stream()
                .map(pluginManagerService::mapStorePluginToDTO)
                .collect(Collectors.toList());
    }

    // Biblioteka z filtrowaniem - również zwraca PluginDTO
    @GetMapping("/library")
    public List<PluginDTO> getPluginsFromLibrary(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String creator,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "10") int page_size,
            @RequestParam int first_id) {

        List<Plugin> library = pluginRepository.findLibrary(name, creator, language, first_id, page_size);
        return library.stream()
                .map(pluginManagerService::mapStorePluginToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/upload")
    public PluginDTO addCustomPlugin(@RequestBody Plugin plugin) {
        Plugin saved = pluginRepository.save(plugin);
        return pluginManagerService.mapStorePluginToDTO(saved);
    }

    @PostMapping("/download/{id}")
    public ResponseEntity<String> downloadPlugin(@PathVariable Long id) {
        try {
            Plugin plugin = pluginRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono pluginu o ID: " + id));

            // Zapisuje plik .py na dysku
            pluginManagerService.savePluginToDisk(plugin);

            return ResponseEntity.ok("Plugin '" + plugin.getName() + "' został pobrany.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Błąd: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public void deletePlugins(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("id");
        pluginRepository.deleteAllByIdInBatch(ids);
    }
}