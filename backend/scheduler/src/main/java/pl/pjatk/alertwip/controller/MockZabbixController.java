package pl.pjatk.alertwip.controller;

import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.service.ZabbixSyncService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/mock/zabbix")
public class MockZabbixController {

    private final ZabbixSyncService zabbixSyncService;

    public MockZabbixController(ZabbixSyncService zabbixSyncService){
        this.zabbixSyncService = zabbixSyncService;
    }
    private boolean simulateOutage = false;

    // Generujemy listę początkowych, sztucznych problemów
    private final List<Map<String, Object>> mockProblems = new ArrayList<>(List.of(
            createMockProblem("1001", "Wysokie zużycie CPU (>90%)", 4, "Sztuczny-Serwer-WWW"),
            createMockProblem("1002", "Brak wolnego miejsca na dysku", 5, "Sztuczny-Serwer-Baza")
    ));

    // --- GŁÓWNY ENDPOINT UDAJĄCY ZABBIXA ---
    @PostMapping
    public Map<String, Object> handleZabbixRpc(@RequestBody Map<String, Object> request) {

        // 1. Testowanie awarii (Connection Timeout)
        if (simulateOutage) {
            throw new RuntimeException("Sztuczna awaria! Zabbix Mock nie odpowiada.");
        }

        // 2. Odpowiadamy na metodę "problem.get"
        String method = (String) request.get("method");
        if ("problem.get".equals(method)) {
            System.out.println("[MOCK ZABBIX] Odebrano zapytanie. Zwracam sztuczne problemy.");
            return Map.of(
                    "jsonrpc", "2.0",
                    "result", mockProblems,
                    "id", request.getOrDefault("id", 1)
            );
        }

        return Map.of("error", "Nieobsługiwana metoda mocka");
    }

    // ==========================================
    // ENDPOINTY DO STEROWANIA SYMULACJĄ (np. z Postmana lub przeglądarki)
    // ==========================================

    @GetMapping("/toggle-outage")
    public String toggleOutage() {
        this.simulateOutage = !this.simulateOutage;
        return "Symulacja awarii serwera: " + (this.simulateOutage ? "WŁĄCZONA" : "WYŁĄCZONA");
    }

    @GetMapping("/add-problem")
    public String addProblem() {
        this.mockProblems.add(createMockProblem(
                String.valueOf(System.currentTimeMillis()), // Unikalne ID
                "Temperatura procesora przekracza 95C",
                4,
                "Sztuczny-Host-XYZ"
        ));

        CompletableFuture.runAsync(() -> {
            try {
                zabbixSyncService.syncAlerts();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return "Dodano nowy problem. Synchronizacja uruchomiona natychmiastowo!";
    }

    @GetMapping("/clear-problems")
    public String clearProblems() {
        this.mockProblems.clear();
        CompletableFuture.runAsync(zabbixSyncService::syncAlerts);

        return "Wyczyszczono problemy. Synchronizacja uruchomiona natychmiastowo!";
    }

    // Pomocnicza metoda generująca JSON-a zgodnego ze strukturą Zabbixa
    private Map<String, Object> createMockProblem(String id, String name, int severity, String host) {
        return Map.of(
                "eventid", id,
                "name", name,
                "severity", severity,
                "hosts", List.of(Map.of("host", host))
        );
    }
}