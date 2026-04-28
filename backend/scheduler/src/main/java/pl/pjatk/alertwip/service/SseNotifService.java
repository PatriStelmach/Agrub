package pl.pjatk.alertwip.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.model.ProblemAction;

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

        emitter.onCompletion(() -> userSubscriptions.remove(emitter));
        emitter.onTimeout(() -> userSubscriptions.remove(emitter));
        emitter.onError((e) -> userSubscriptions.remove(emitter));

        userSubscriptions.put(emitter, groups);

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
            boolean hasAccess = alert.getTechnicianGroups().stream()
                    .anyMatch(userGroups::contains);

            if (hasAccess) {
                try {
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                            .id(String.valueOf(alert.getId()))
                            .name("message") // Vue onmessage reaguje na ten typ
                            .data(Map.of(
                                    "status", "success",
                                    "eventType", status,       // "NEW_ALERT" lub "ALERT_RESOLVED"
                                    "notification", alert.isRequiresNotification(),
                                    "message", alert           // Obiekt alertu
                            ), MediaType.APPLICATION_JSON);

                    emitter.send(event);

                    // Opcjonalnie logujemy sukces w konsoli
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

    public void sendAlert(String status, ProblemAction action) {
        GlobalProblem alert = action.getProblem(); // Wyciągamy alert z akcji, żeby sprawdzić grupy!
        List<SseEmitter> deadEmitters = new ArrayList<>();

        userSubscriptions.forEach((emitter, userGroups) -> {
            boolean hasAccess = alert.getTechnicianGroups().stream()
                    .anyMatch(userGroups::contains);

            if (hasAccess) {
                try {
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                            .id(String.valueOf(action.getId()))
                            .name("message")
                            .data(Map.of(
                                    "status", "success",
                                    "eventType", status,       // np. "NEW_ACTION"
                                    "notification", false,     // Nie chcemy głośnego dźwięku na każdy komentarz
                                    "message", action          // Wysyłamy sam obiekt akcji
                            ), MediaType.APPLICATION_JSON);

                    emitter.send(event);
                    System.out.println("[SSE] Wysłano nową akcję (" + status + ") do alertu: " + alert.getUniqueKey());

                } catch (Exception e) {
                    deadEmitters.add(emitter);
                }
            }
        });

        deadEmitters.forEach(userSubscriptions::remove);
    }
}