package pl.pjatk.alertwip.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;

import java.time.LocalDateTime;

@Service
public class WazuhSyncService {

    private final WazuhApiService wazuhApi;
    private final GlobalProblemRepository problemRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public WazuhSyncService(WazuhApiService wazuhApi, GlobalProblemRepository problemRepository) {
        this.wazuhApi = wazuhApi;
        this.problemRepository = problemRepository;
    }

    @Scheduled(fixedDelay = 30000)
    public void sync() {
        System.out.println("[WAZUH SYNC] Pobieranie logów operacyjnych...");
        String token = wazuhApi.getAuthToken();
        if (token == null) {
            System.err.println("[WAZUH SYNC] Błąd: Brak tokenu autoryzacyjnego.");
            return;
        }

        String rawJson = wazuhApi.fetchAlerts(token); // endpoint /manager/logs

        if (rawJson == null) return;

        try {
            JsonNode logs = mapper.readTree(rawJson).path("data").path("affected_items");

            if (logs.isArray() && !logs.isEmpty()) {
                int added = 0;
                for (JsonNode logNode : logs) {

                    String timestamp = logNode.path("timestamp").asText("");
                    String tag = logNode.path("tag").asText("");
                    String level = logNode.path("level").asText("");
                    String description = logNode.path("description").asText("");

                    String logText = String.format("[%s] %s (%s): %s", timestamp, tag, level, description);

                    if (logText.trim().length() < 10) continue;

                    String uniqueKey = "[WAZUH] - Hash: " + logText.hashCode();

                    if (!problemRepository.existsByUniqueKey(uniqueKey)) {
                        GlobalProblem problem = new GlobalProblem();
                        problem.setUniqueKey(uniqueKey);
                        problem.setSubject("Wazuh Raw Log");

                        if (logText.contains("prod-agent-01")) {
                            problem.setSource("prod-agent-01");
                        } else {
                            problem.setSource("wazuh-manager");
                        }

                        problem.setOriginType("WAZUH");
                        problem.setMessage(logText);

                        // Jeżeli opis zawiera informacje o błędzie / włamaniu - podnosimy severity
                        if (logText.toUpperCase().contains("ERROR") || logText.toUpperCase().contains("CRITICAL")) {
                            problem.setSeverity(4);
                        } else if(logText.toUpperCase().contains("WARNING")) {
                            problem.setSeverity(3);
                        } else {
                            problem.setSeverity(2);
                        }

                        problem.setStatus("New");
                        problem.setCreatedAt(LocalDateTime.now());

                        problemRepository.save(problem);
                        added++;
                    }
                }

                if (added > 0) {
                    System.out.println("[WAZUH SYNC] Zapisano " + added + " nowych logów z Wazuha do bazy!");
                } else {
                    System.out.println("[WAZUH SYNC] Brak nowych logów do dodania (wszystkie widoczne w API są już w bazie).");
                }
            } else {
                System.out.println("[WAZUH SYNC] API nie zwróciło żadnych logów w strukturze data -> affected_items. (Array jest pusty)");
            }
        } catch (Exception e) {
            System.err.println("[WAZUH SYNC] Błąd parsowania: " + e.getMessage());
        }
    }
}