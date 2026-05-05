package pl.pjatk.alertwip.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.PluginDTO;
import pl.pjatk.alertwip.model.Plugin;
import pl.pjatk.alertwip.repository.PluginRepository;
import pl.pjatk.alertwip.service.PluginManagerService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/plugins")
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

    // --- ZAKTUALIZOWANA BIBLIOTEKA ---
    // Zmieniamy sygnaturę z List<PluginDTO> na ResponseEntity<Page<PluginDTO>>
    @GetMapping("/library")
    public ResponseEntity<Page<PluginDTO>> getPluginsFromLibrary(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String creator,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "0") int page,       // Zastępuje first_id
            @RequestParam(defaultValue = "10") int pageSize   // Zastępuje page_size
    ) {
        // Domyślnie sortujemy od najnowszych wtyczek
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        // Pobieramy "stronę" encji z repozytorium
        Page<Plugin> libraryPage = pluginRepository.findLibrary(name, creator, language, pageable);

        // Mapujemy całą stronę encji na stronę obiektów DTO za pomocą metody z Twojego serwisu
        Page<PluginDTO> dtoPage = libraryPage.map(pluginManagerService::mapStorePluginToDTO);

        return ResponseEntity.ok(dtoPage);
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