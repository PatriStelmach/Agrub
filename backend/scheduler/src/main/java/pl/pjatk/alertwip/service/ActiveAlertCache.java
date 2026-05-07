package pl.pjatk.alertwip.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActiveAlertCache {

    private final GlobalProblemRepository problemRepository;

    private final Map<Long, GlobalProblem> activeAlerts = new ConcurrentHashMap<>();

    public ActiveAlertCache(GlobalProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @PostConstruct
    public void initCache() {
        List<GlobalProblem> unresolvedProblems = problemRepository.findAll().stream()
                .filter(p -> !"Done".equals(p.getStatus()))
                .toList();

        for (GlobalProblem gp : unresolvedProblems) {
            activeAlerts.put(gp.getId(), gp);
        }
        System.out.println("[CACHE] Załadowano " + activeAlerts.size() + " aktywnych alertów do pamięci RAM.");
    }

    // Dodanie lub aktualizacja alertu
    public void updateAlert(GlobalProblem problem) {
        if ("Done".equals(problem.getStatus())) {
            activeAlerts.remove(problem.getId());
        } else {
            activeAlerts.put(problem.getId(), problem);
        }
    }

    // Usunięcie
    public void removeAlert(Long id) {
        activeAlerts.remove(id);
    }

    // Pobranie z pamięciNA
    public List<GlobalProblem> getActiveAlertsForGroups(List<String> userGroups) {
        // Admin widzi wszystko
        if (userGroups.contains("ADMIN")) {
            return activeAlerts.values().stream().toList();
        }

        return activeAlerts.values().stream()
                .filter(p -> p.getTechnicianGroups().stream().anyMatch(userGroups::contains))
                .toList();
    }
}