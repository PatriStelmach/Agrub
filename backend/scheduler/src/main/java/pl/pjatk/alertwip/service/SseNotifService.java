package pl.pjatk.alertwip.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.dto.AlertUpdateEventDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseNotifService {

    private final Map<SseEmitter, List<String>> userSubscriptions = new ConcurrentHashMap<>();

    public SseEmitter subscribe(List<String> groups) {
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
                            .name("message")
                            .data(Map.of(
                                    "status", "success",
                                    "eventType", status,
                                    "notification", alert.isRequiresNotification(),
                                    "message", alert
                            ), MediaType.APPLICATION_JSON);

                    emitter.send(event);
                    System.out.println("[SSE] Wysłano " + status + " dla alertu: " + alert.getUniqueKey());

                } catch (Exception e) {
                    deadEmitters.add(emitter);
                }
            }
        });

        deadEmitters.forEach(userSubscriptions::remove);
    }

    // wysyłamy tylko różnice a nie cały
    public void sendAlertUpdate(String status, AlertUpdateEventDTO payload, GlobalProblem alertContext) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        userSubscriptions.forEach((emitter, userGroups) -> {
            // Używamy "alertContext", żeby sprawdzić, czy użytkownik ma uprawnienia do tego konkretnego alertu
            boolean hasAccess = alertContext.getTechnicianGroups().stream()
                    .anyMatch(userGroups::contains);

            if (hasAccess) {
                try {
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                            .id(String.valueOf(payload.alertId())) // ID głównego alertu
                            .name("message")
                            .data(Map.of(
                                    "status", "success",
                                    "eventType", status,
                                    "notification", false,
                                    "message", payload
                            ), MediaType.APPLICATION_JSON);

                    emitter.send(event);
                    System.out.println("[SSE] Wysłano aktualizację (Diff) do alertu ID: " + payload.alertId());

                } catch (Exception e) {
                    deadEmitters.add(emitter);
                }
            }
        });

        deadEmitters.forEach(userSubscriptions::remove);
    }
}