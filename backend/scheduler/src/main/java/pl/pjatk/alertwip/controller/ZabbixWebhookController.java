package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/zabbix")
public class ZabbixWebhookController {

    @PostMapping("/alert")
    public ResponseEntity<String> handleZabbixAlert(@RequestBody Map<String, Object> payload) {
        String eventStatus = (String) payload.get("status");
        String eventName = (String) payload.get("name");
        String host = (String) payload.get("host");

        System.out.println("[ZABBIX WEBHOOK] " + eventStatus + ": " + eventName + " na hoście " + host);

        // Tutaj możesz dodać logikę zapisu do bazy lub powiadomienia użytkownika
        return ResponseEntity.ok("Alert processed");
    }
}