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
                <title>SSE Debugger (Secured)</title>
                <style>
                    body { font-family: monospace; background: #1a1a1a; color: #00ff00; padding: 20px; }
                    .log { border-bottom: 1px solid #333; padding: 5px; margin: 5px 0; }
                    .status { color: gold; font-weight: bold; }
                    .error { color: red; }
                </style>
            </head>
            <body>
                <h2>SSE Real-time Monitor (Secured JWT)</h2>
                <div id="status" class="status">1. Logowanie jako admin@pjatk.pl...</div>
                <div id="logs"></div>

                <script type="module">
                    import { fetchEventSource } from 'https://esm.sh/@microsoft/fetch-event-source';

                    const logs = document.getElementById('logs');
                    const status = document.getElementById('status');

                    function logMessage(msg, isError = false) {
                        const div = document.createElement('div');
                        div.className = isError ? 'log error' : 'log';
                        const time = new Date().toLocaleTimeString();
                        div.innerText = `[${time}] ${msg}`;
                        logs.prepend(div);
                    }

                    async function connect() {
                        try {
                            // KROK 1: Autoryzacja i pobranie tokena
                            const loginResponse = await fetch('/api/auth/login', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ 
                                    email: 'admin@pjatk.pl', 
                                    password: 'admin123' 
                                })
                            });

                            if (!loginResponse.ok) {
                                throw new Error('Błąd logowania. Czy baza danych ma dodanego tego użytkownika?');
                            }

                            const authData = await loginResponse.json();
                            // Zakładam, że w obiekcie DTO z logowania zmienna to 'token'
                            const token = authData.token; 

                            status.innerText = "2. ZALOGOWANO. Łączenie z SSE...";
                            logMessage(`Uzyskano token JWT: ${token.substring(0, 20)}...`);

                            // KROK 2: Połączenie z SSE za pomocą tokena JWT w nagłówku!
                            fetchEventSource('/api/alerts/stream', {
                                headers: {
                                    'Authorization': `Bearer ${token}`
                                },
                                openWhenHidden: true,
                                onopen(response) {
                                    if (response.ok) {
                                        status.innerText = "3. POŁĄCZONO (Oczekiwanie na alerty...)";
                                        return;
                                    }
                                    status.innerText = `BŁĄD POŁĄCZENIA: ${response.status}`;
                                },
                                onmessage(event) {
                                    logMessage(`ODEBRANO [Typ zdarzenia: ${event.event || 'message'}]: ${event.data}`);
                                    console.log("Surowe dane:", event.data);
                                },
                                onerror(err) {
                                    status.innerText = "BŁĄD POŁĄCZENIA / ROZŁĄCZONO";
                                    console.error("SSE Error:", err);
                                    throw err; // Wyrzucenie błędu automatycznie ponowi próbę połączenia
                                }
                            });

                        } catch (error) {
                            status.innerText = "BŁĄD KRYTYCZNY";
                            logMessage(error.message, true);
                        }
                    }

                    // Uruchamiamy proces
                    connect();
                </script>
            </body>
            </html>
            """;
    }
}