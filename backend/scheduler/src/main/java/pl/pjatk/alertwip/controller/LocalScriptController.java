package pl.pjatk.alertwip.controller;

import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.model.LocalScriptDTO;
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

    @GetMapping("/list")
    public List<LocalScriptDTO> getMyLocalScripts() {
        // Zwraca listę wszystkiego, co fizycznie leży w folderze scripts/
        return pythonScriptService.listLocalScripts();
    }
}