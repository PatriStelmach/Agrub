package pl.pjatk.alertwip.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            // Ładowanie pliku z resources
            InputStream serviceAccount = getClass()
                    .getClassLoader()
                    .getResourceAsStream("firebase_config.json");

            if (serviceAccount == null) {
                System.err.println("FCM: Service account key file not found");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("FCM:  Firebase Admin SDK initialized");

            }
        } catch (IOException e) {
            System.err.println("FCM: Initialization error: " + e.getMessage());
        }
    }
}