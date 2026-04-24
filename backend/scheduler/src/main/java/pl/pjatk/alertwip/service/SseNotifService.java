package pl.pjatk.alertwip.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pjatk.alertwip.model.GlobalProblem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseNotifService {

    // Używamy bezpiecznej kolekcji dla środowisk wielowątkowych
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        // Long.MAX_VALUE oznacza, że połączenie nie zostanie zerwane przez timeout serwera
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        // Usuwanie "martwych" połączeń po rozłączeniu klienta (Vue)
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        return emitter;
    }

    public void sendAlert(String eventStatus, GlobalProblem alert) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        // Budujemy żądany obiekt JSON zawierający "status" i "message"
        Map<String, Object> payload = Map.of(
                "status", eventStatus, // Będzie to np. "NEW_ALERT" lub "ALERT_RESOLVED"
                "message", alert       // Cały obiekt GlobalProblem
        );

        for (SseEmitter emitter : emitters) {
            try {
                // Wysyłamy zwykły, bezimienny strumień danych (domyślny event SSE),
                // który zrzuci naszą Mapę do ładnego JSON-a
                emitter.send(payload, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                // Jeśli nie da się wysłać (np. ktoś zamknął przeglądarkę brutualnie), oznacz do usunięcia
                deadEmitters.add(emitter);
            }
        }

        // Czyścimy listę z nieaktywnych klientów
        emitters.removeAll(deadEmitters);
    }
}