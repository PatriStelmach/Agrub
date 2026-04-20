package pl.pjatk.alertwip.controller;

import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.PluginDTO;
import pl.pjatk.alertwip.dto.PluginDetailsDTO;
import pl.pjatk.alertwip.service.PythonScriptService;

import java.util.List;

@RestController
@RequestMapping("/api/local-scripts")
@CrossOrigin(origins = "*")
public class LocalScriptController {

    private final PythonScriptService pythonScriptService;

    public LocalScriptController(PythonScriptService pythonScriptService) {
        this.pythonScriptService = pythonScriptService;
    }

    // Lista wszystkich plików na dysku + dane z ScheduledTask (jeśli istnieją)
    @GetMapping("/list")
    public List<PluginDTO> getMyLocalScripts() {
        return pythonScriptService.listLocalScripts();
    }

    // Podgląd kodu i opisu na podstawie nazwy pliku (nie ID!)
    // Regex {fileName:.+} pozwala na przesyłanie kropek w nazwie pliku
    @GetMapping("/{fileName:.+}/details")
    public PluginDetailsDTO getDetails(@PathVariable String fileName) {
        return pythonScriptService.getPluginDetailsByFileName(fileName);
    }
}