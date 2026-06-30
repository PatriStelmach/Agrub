package pl.pjatk.alertwip.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.dto.AlertUpdateEventDTO;
import pl.pjatk.alertwip.controller.MobileDeviceController;

import java.util.*;
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


        //FCM

        //         Set<String> fcmTokens = new HashSet<>(Set.of());
        //        for (String user: MobileDeviceController.tokenStorage.keySet()) {
        //            fcmTokens.addAll(MobileDeviceController.getTokenForUser(user));
        //        }

        String testUser = "admin@pjatk.pl";
        Set<String> fcmTokens = MobileDeviceController.getTokenForUser(testUser);

            Set<String> tokensToRemove = new HashSet<>();
            for (String token : fcmTokens) {

                try {
                    String alertIdStr = String.valueOf(alert.getId());


                String severityIndex = String.valueOf(alert.getSeverity());
                System.out.printf("severityIndex: %s", severityIndex);
                String statusStr = alert.getStatus() != null ? alert.getStatus().toLowerCase() : "sent";
                System.out.printf("statusStr: %s", statusStr);
                String createdAtStr = alert.getCreatedAt() != null ? alert.getCreatedAt().toString() : java.time.Instant.now().toString();
                System.out.printf("createdAtStr: %s", createdAtStr);

                    Message message = Message.builder()
                            .putData("title", "Zhakowali nas znowu")
                            .putData("body", "Szykuj bitcoiny " + alert.getUniqueKey())

                            .putData("id", alertIdStr)
                            .putData("alertId", alertIdStr) //
                            .putData("subject", alert.getSubject() != null ? alert.getSubject() : alert.getUniqueKey())
                            .putData("source", alert.getSource() != null ? alert.getSource() : "System")
                            .putData("severity", severityIndex)
                            .putData("status", statusStr)
                            .putData("createdAt", createdAtStr)
                            .putData("message", alert.getMessage() != null ? alert.getMessage() : "")
                            .putData("acknowledged", String.valueOf(alert.isAcknowledged()))
                            .setToken(token)
                            .build();

                    FirebaseMessaging.getInstance().send(message);
                    System.out.println("[FCM] Wysłano powiadomienie wybudzające do tokenu: " + token);

                } catch (FirebaseMessagingException e) {
                    MessagingErrorCode errorCode = e.getMessagingErrorCode();

                    if (errorCode == MessagingErrorCode.UNREGISTERED ||
                            errorCode == MessagingErrorCode.INVALID_ARGUMENT) {

                        System.err.println("[FCM CLEANUP] Nieaktywny token dodany do usunięcia: " + token);
                        tokensToRemove.add(token);
                }

                } catch (Exception e) {
                    System.err.println("[FCM ERROR] Nie udało się wysłać powiadomienia push: " + e.getMessage());
                }
                }
        System.err.println("[FCM CLEANUP] Usuwam nieaktywny tokeny: ");
        tokensToRemove.forEach(MobileDeviceController::removeToken);

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