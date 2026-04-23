package pl.pjatk.alertwip.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mock/zabbix")
public class MockZabbixController {

    // Zmienne sterujące naszym sztucznym Zabbixem
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

    @GetMapping("/clear-problems")
    public String clearProblems() {
        this.mockProblems.clear();
        return "Usunięto wszystkie problemy w Mocku. Następny cykl (co 60s) wyśle ALERT_RESOLVED.";
    }

    @GetMapping("/add-problem")
    public String addProblem() {
        this.mockProblems.add(createMockProblem("1003", "Temperatura procesora przekracza 95C", 4, "Sztuczny-Host-XYZ"));
        return "Dodano nowy problem. Następny cykl (co 60s) wyśle NEW_ALERT.";
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