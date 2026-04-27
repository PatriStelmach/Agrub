package pl.pjatk.alertwip.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pjatk.alertwip.model.GlobalProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseNotifService {

    // Mapa przechowująca aktywne połączenia oraz przypisane do nich grupy uprawnień
    private final Map<SseEmitter, List<String>> userSubscriptions = new ConcurrentHashMap<>();

    public SseEmitter subscribe(List<String> groups) {
        // Ustawiamy timeout na -1 (nieskończoność), aby połączenie nie wygasło samo z siebie
        SseEmitter emitter = new SseEmitter(-1L);

        // Usuwamy subskrypcję przy błędzie lub zakończeniu
        emitter.onCompletion(() -> userSubscriptions.remove(emitter));
        emitter.onTimeout(() -> userSubscriptions.remove(emitter));
        emitter.onError((e) -> userSubscriptions.remove(emitter));

        userSubscriptions.put(emitter, groups);

        // Wysyłamy wiadomość powitalną, aby natychmiast otworzyć "rurę" w przeglądarce
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected to Alert Stream")
                    .build());
        } catch (Exception e) {
            userSubscriptions.remove(emitter);
        }

        return emitter;
    }

    public void sendAlert(String status, GlobalProblem alert) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        userSubscriptions.forEach((emitter, userGroups) -> {
            // Sprawdzamy, czy grupy użytkownika pokrywają się z grupami alertu
            boolean hasAccess = alert.getTechnicianGroups().stream()
                    .anyMatch(userGroups::contains);

            if (hasAccess) {
                try {
                    // Używamy SseEventBuilder, aby wymusić poprawny format i brak buforowania
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                            .id(String.valueOf(alert.getId()))
                            .name("message") // Vue onmessage reaguje na ten typ
                            .data(Map.of(
                                    "status", status,
                                    "notification", alert.isRequiresNotification(),
                                    "message", alert
                            ), MediaType.APPLICATION_JSON);

                    emitter.send(event);

                    // Opcjonalnie logujemy sukces w konsoli IntelliJ
                    System.out.println("[SSE] Wysłano " + status + " dla alertu: " + alert.getUniqueKey());

                } catch (Exception e) {
                    // Jeśli wysyłanie się nie uda (np. przeglądarka zamknięta), oznaczamy jako martwe
                    deadEmitters.add(emitter);
                }
            }
        });

        // Sprzątamy połączenia, które rzuciły błędem
        deadEmitters.forEach(userSubscriptions::remove);
    }
}