package pl.pjatk.alertwip.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ExecutorConfig {
    @Bean
    public ThreadPoolTaskExecutor scriptExecutor(
            @Value("${app.executor.core-size}") int core,
            @Value("${app.executor.max-size}") int max,
            @Value("${app.executor.queue-capacity}") int queue) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(max);
        executor.setQueueCapacity(queue);
        executor.setThreadNamePrefix("AlertWorker-");
        executor.initialize();
        return executor;
    }
}