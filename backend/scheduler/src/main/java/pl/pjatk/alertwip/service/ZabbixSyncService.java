package pl.pjatk.alertwip.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ZabbixSyncService {

    private final ZabbixApiService zabbixApiService;

    public ZabbixSyncService(ZabbixApiService zabbixApiService) {
        this.zabbixApiService = zabbixApiService;
    }

    @Scheduled(fixedRate = 60000) // Co 60 sekund
    public void syncAlerts() {
        System.out.println("[ZABBIX SYNC] Rozpoczynam synchronizację...");
        try {
            List<Map<String, Object>> currentProblems = zabbixApiService.getActiveProblems();

            if (currentProblems != null) {
                System.out.println("[ZABBIX SYNC] Liczba aktywnych problemów: " + currentProblems.size());
                // Porównaj currentProblems ze stanem w swojej bazie danych (np. tabela TaskExecutionLog)
            }
        } catch (Exception e) {
            System.err.println("[ZABBIX SYNC] Błąd podczas synchronizacji: " + e.getMessage());
            // Możesz spróbować zalogować się ponownie przy kolejnej iteracji
        }
    }
}