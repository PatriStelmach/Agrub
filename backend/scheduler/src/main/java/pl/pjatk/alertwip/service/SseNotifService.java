package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseNotifService {

    // CopyOnWriteArrayList jest kluczowe! Zapobiega błędom ConcurrentModificationException,
    // gdy jeden wątek dodaje użytkownika, a inny w tym samym czasie wysyła alert.
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        // Ustawiamy brak timeoutu (Long.MAX_VALUE), żeby przeglądarka nie rozłączała się co 30 sekund
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        // Sprzątanie po odłączeniu użytkownika (np. zamknięcie karty w przeglądarce)
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        return emitter;
    }

    public void sendAlert(String eventName, Object payload) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        emitters.forEach(emitter -> {
            try {
                // Wysyłamy zdarzenie. Spring automatycznie zamieni 'payload' na JSON.
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(payload));
            } catch (IOException e) {
                // Jeśli wysłanie się nie powiodło (użytkownik uciekł, a serwer jeszcze tego nie wie),
                // oznaczamy go do usunięcia.
                deadEmitters.add(emitter);
            }
        });

        // Usuwamy martwe połączenia
        emitters.removeAll(deadEmitters);
    }
}