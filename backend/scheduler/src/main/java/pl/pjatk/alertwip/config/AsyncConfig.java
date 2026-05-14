package pl.pjatk.alertwip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Ta klasa służy tylko jako włącznik dla metod @Async w całym systemie
}