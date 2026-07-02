package pl.pjatk.alertwip.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.PluginDTO;
import pl.pjatk.alertwip.model.Plugin;
import pl.pjatk.alertwip.repository.PluginRepository;
import pl.pjatk.alertwip.repository.spec.PluginSpecification;
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

    // ==========================================
    // POBIERANIE WSZYSTKICH PLUGINÓW
    // ==========================================
    @GetMapping("/all")
    public List<PluginDTO> getAllMyPlugins() {
        return pluginRepository.findAll().stream()
                .map(pluginManagerService::mapStorePluginToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // SZCZEGÓŁY PLUGINU (OPIS I KOD)
    // ==========================================
    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, String>> getPluginDetails(@PathVariable Long id) {
        return pluginRepository.findById(id)
                .map(plugin -> ResponseEntity.ok(Map.of(
                        "description", plugin.getDescription() != null ? plugin.getDescription() : "",
                        "code", plugin.getCode() != null ? plugin.getCode() : ""
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // POBIERANIE UNIKALNYCH TAGÓW
    // ==========================================
    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        List<String> tags = pluginRepository.findAllUniqueTags();
        return ResponseEntity.ok(tags);
    }

    // ==========================================
    // WYSZUKIWARKA
    // ==========================================
    @GetMapping("/library")
    public ResponseEntity<Page<PluginDTO>> getPluginsFromLibrary(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String creator,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) Integer maxWeight,
            @RequestParam(defaultValue = "id") String sortKey,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortKey));
        Specification<Plugin> spec = PluginSpecification.filterLibrary(name, creator, language, tags, maxWeight);
        Page<Plugin> libraryPage = pluginRepository.findAll(spec, pageable);
        Page<PluginDTO> dtoPage = libraryPage.map(pluginManagerService::mapStorePluginToDTO);

        //test logging
        System.out.println("\n=== [API /library] WYSYŁKA DO FRONTENDU ===");
        System.out.println("Filtry -> tags: " + tags + ", maxWeight: " + maxWeight + "KB, sort: " + sortKey + " " + sortOrder.toUpperCase());
        System.out.println("Wyników na tej stronie: " + dtoPage.getNumberOfElements() + " / Razem: " + dtoPage.getTotalElements());

        for (PluginDTO dto : dtoPage.getContent()) {
            System.out.println(String.format(" -> id: %-4d | fileName: %-12s | weight: %-2d KB | updatedAt: %-19s | tags: %s",
                    dto.id(),
                    dto.fileName(),
                    dto.weight(),
                    dto.updatedAt() != null ? dto.updatedAt() : "null",
                    dto.tags()));
        }
        System.out.println("===========================================\n");

        return ResponseEntity.ok(dtoPage);
    }

    // ==========================================
    // DODAWANIE NOWEGO PLUGINU DO SKLEPU
    // ==========================================
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public PluginDTO addCustomPlugin(@RequestBody Plugin plugin) {
        Plugin saved = pluginRepository.save(plugin);
        return pluginManagerService.mapStorePluginToDTO(saved);
    }

    // ==========================================
    // POBIERANIE (INSTALACJA) PLUGINU NA DYSK
    // ==========================================
    @PostMapping("/download/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<String> downloadPlugin(@PathVariable Long id) {
        try {
            Plugin plugin = pluginRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono pluginu o ID: " + id));

            pluginManagerService.savePluginToDisk(plugin);

            return ResponseEntity.ok("Plugin '" + plugin.getName() + "' został pobrany.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Błąd: " + e.getMessage());
        }
    }

    // ==========================================
    // USUWANIE PLUGINÓW ZE SKLEPU
    // ==========================================
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public void deletePlugins(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("id");
        if (ids != null && !ids.isEmpty()) {
            pluginRepository.deleteAllByIdInBatch(ids);
        }
    }
}