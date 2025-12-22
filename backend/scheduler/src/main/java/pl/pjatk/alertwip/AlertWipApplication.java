package pl.pjatk.alertwip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlertWipApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertWipApplication.class, args);
    }

}
