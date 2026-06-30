package pl.pjatk.alertwip.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.MobileDeviceTokenDTO;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequestMapping("/api/devices")
public class MobileDeviceController {

    public static final Map<String, Set<String>> tokenStorage = new ConcurrentHashMap<>();



    @PostMapping("/token")
    public ResponseEntity<Void> registerDeviceToken(
            @RequestBody MobileDeviceTokenDTO dto,
            Principal principal) {

        String username = principal.getName();
        String fcmToken = dto.token();

        tokenStorage.computeIfAbsent(username, k -> new CopyOnWriteArraySet<>())
                .add(fcmToken);

        System.out.println("=============================================");
        System.out.println("FCM MEMORY STORAGE DEBUG:");
        System.out.println("User: " + username);
        System.out.println("Registered token: " + fcmToken);
        System.out.println("Storage state: " + tokenStorage.get(username));
        System.out.println("=============================================");

        return ResponseEntity.ok().build();
    }


    public static Set<String> getTokenForUser(String username) {
        return tokenStorage.getOrDefault(username, Set.of());
    }
    public static void removeToken(String token) {
        tokenStorage.forEach((username, tokens) -> tokens.remove(token));
        tokenStorage.values().removeIf(Set::isEmpty);
    }

}
