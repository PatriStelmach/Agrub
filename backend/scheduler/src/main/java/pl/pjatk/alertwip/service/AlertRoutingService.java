package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.model.AlertRule;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.model.MatchType;
import pl.pjatk.alertwip.repository.AlertRuleRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
public class AlertRoutingService {
    private final AlertRuleRepository ruleRepository;
    // Cache kompilowanych wyrażeń regularnych dla maksymalnej wydajności
    private final Map<String, Pattern> regexCache = new ConcurrentHashMap<>();

    public AlertRoutingService(AlertRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public void processVisibility(GlobalProblem alert) {
        Set<String> targetGroups = new HashSet<>();

        // Domyślna grupa, która zawsze wszystko widzi
        targetGroups.add("ADMIN");
        boolean shouldPlaySound = false;

        List<AlertRule> rules = ruleRepository.findAll();

        for (AlertRule rule : rules) {
            if (evaluateRule(rule, alert)) {
                targetGroups.add(rule.getUserGroup().getName());

                if (rule.isPlaySound()) {
                    shouldPlaySound = true;
                }
            }
        }

        alert.setTECHNICIANGroups(new ArrayList<>(targetGroups));
        alert.setRequiresNotification(shouldPlaySound);
    }

    private boolean evaluateRule(AlertRule rule, GlobalProblem alert) {
        // 1. Tani warunek (Severity)
        if (alert.getSeverity() < rule.getMinSeverity()) return false;

        // 2. Warunek źródła (Host)
        if (!isMatch(alert.getSource(), rule.getSourcePattern(), rule.getSourceType())) {
            return false;
        }

        // 3. Warunek treści komunikatu
        return isMatch(alert.getMessage(), rule.getContentPattern(), rule.getContentType());
    }

    private boolean isMatch(String value, String pattern, MatchType type) {
        // Pusty wzorzec w regule traktujemy jako "Zezwól na wszystko" w tym polu
        if (pattern == null || pattern.isEmpty()) return true;
        if (value == null) return false;

        return switch (type) {
            case EXACT -> value.equals(pattern);
            case STARTS_WITH -> value.startsWith(pattern);
            case ENDS_WITH -> value.endsWith(pattern);
            case CONTAINS -> value.contains(pattern);
            case REGEX -> getCachedPattern(pattern).matcher(value).find();
        };
    }

    private Pattern getCachedPattern(String regex) {
        return regexCache.computeIfAbsent(regex, Pattern::compile);
    }
}