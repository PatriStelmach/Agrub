package pl.pjatk.alertwip.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SseTestController {

    @GetMapping("/test-sse")
    public String getTestPage() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>SSE Debugger</title>
                <style>
                    body { font-family: monospace; background: #1a1a1a; color: #00ff00; padding: 20px; }
                    .log { border-bottom: 1px solid #333; padding: 5px; margin: 5px 0; }
                    .status { color: gold; font-weight: bold; }
                </style>
            </head>
            <body>
                <h2>SSE Real-time Monitor (Grupa: ADMIN)</h2>
                <div id="status" class="status">Łączenie...</div>
                <div id="logs"></div>

                <script>
                    // Łączymy się dokładnie pod ten sam adres co Twoje Vue
                    const evtSource = new EventSource('/api/alerts/stream?groups=ADMIN');
                    const logs = document.getElementById('logs');
                    const status = document.getElementById('status');

                    evtSource.onopen = () => status.innerText = "POŁĄCZONO (Oczekiwanie na alerty...)";
                    evtSource.onerror = () => status.innerText = "BŁĄD POŁĄCZENIA / ROZŁĄCZONO";

                    evtSource.onmessage = (event) => {
                        const div = document.createElement('div');
                        div.className = 'log';
                        const time = new Date().toLocaleTimeString();
                        div.innerText = `[${time}] ODEBRANO: ${event.data}`;
                        logs.prepend(div);
                        console.log("Surowe dane:", event.data);
                    };
                </script>
            </body>
            </html>
            """;
    }
}