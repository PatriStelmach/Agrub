package pl.pjatk.alertwip;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import pl.pjatk.alertwip.service.PythonScriptService;

@Component
public class AlertManager {

    private final PythonScriptService pythonService;
    private final ThreadPoolTaskExecutor scriptExecutor;

    public AlertManager(PythonScriptService pythonService, ThreadPoolTaskExecutor scriptExecutor) {
        this.pythonService = pythonService;
        this.scriptExecutor = scriptExecutor;
    }

    @Scheduled(fixedRate = 30000)
    public void monitorServers() {
        String[] servers = {"10.0.0.1", "10.0.0.2", "10.0.0.3"};

        for (String ip : servers) {
            // Każda iteracja wysyła zadanie do puli executor.
            // Jeśli MaxPoolSize na to pozwala, każdy skrypt dostanie własny wątek.
            scriptExecutor.execute(() -> pythonService.runScript("check_ping.py", ip));
        }
    }
}