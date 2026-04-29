package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.service.WazuhApiService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wazuh")
public class WazuhController {

    private final WazuhApiService wazuhApi;
    private final GlobalProblemRepository problemRepository;

    public WazuhController(WazuhApiService wazuhApi, GlobalProblemRepository problemRepository) {
        this.wazuhApi = wazuhApi;
        this.problemRepository = problemRepository;
    }

    @GetMapping("/agents")
    public ResponseEntity<String> getAgents() {
        String token = wazuhApi.getAuthToken();
        if (token == null) return ResponseEntity.status(500).body("{\"error\": \"Brak tokenu Wazuh\"}");
        return ResponseEntity.ok(wazuhApi.fetchAgents(token));
    }

    @GetMapping("/logs/{sourceName}")
    public ResponseEntity<List<GlobalProblem>> getLogsBySource(@PathVariable String sourceName) {
        return ResponseEntity.ok(problemRepository.findBySource(sourceName));
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_alerts", problemRepository.count());
        stats.put("new_alerts", problemRepository.countByStatus("New"));
        stats.put("critical_alerts", problemRepository.countBySeverityGreaterThanEqual(4));
        // Możesz tu dodać więcej danych
        return ResponseEntity.ok(stats);
    }

    @PatchMapping("/alerts/{id}/status")
    public ResponseEntity<GlobalProblem> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return problemRepository.findById(id).map(problem -> {
            problem.setStatus(status);
            if ("Resolved".equalsIgnoreCase(status)) {
                problem.setAcknowledged(true);
            }
            return ResponseEntity.ok(problemRepository.save(problem));
        }).orElse(ResponseEntity.notFound().build());
    }
}