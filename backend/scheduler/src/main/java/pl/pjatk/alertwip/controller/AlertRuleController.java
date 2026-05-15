package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.AlertRuleRequestDTO;
import pl.pjatk.alertwip.model.AlertRule;
import pl.pjatk.alertwip.model.UserGroup;
import pl.pjatk.alertwip.repository.AlertRuleRepository;
import pl.pjatk.alertwip.repository.UserGroupRepository;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class AlertRuleController {

    private final AlertRuleRepository alertRuleRepository;
    private final UserGroupRepository userGroupRepository;

    public AlertRuleController(AlertRuleRepository alertRuleRepository, UserGroupRepository userGroupRepository) {
        this.alertRuleRepository = alertRuleRepository;
        this.userGroupRepository = userGroupRepository;
    }

    // 1. Pobieranie wszystkich reguł
    @GetMapping
    public ResponseEntity<List<AlertRule>> getAllRules() {
        return ResponseEntity.ok(alertRuleRepository.findAll());
    }

    // 2. Dodawanie nowej reguły
    @PostMapping
    public ResponseEntity<?> createRule(@RequestBody AlertRuleRequestDTO request) {
        // Szukamy grupy, do której ma być przypisana reguła
        UserGroup group = userGroupRepository.findById(request.userGroupId())
                .orElse(null);

        if (group == null) {
            return ResponseEntity.badRequest().body("Nie znaleziono grupy o podanym ID");
        }

        AlertRule rule = new AlertRule();
        rule.setUserGroup(group);
        rule.setSourcePattern(request.sourcePattern());
        rule.setSourceType(request.sourceType() != null ? request.sourceType() : pl.pjatk.alertwip.model.MatchType.CONTAINS);
        rule.setContentPattern(request.contentPattern());
        rule.setContentType(request.contentType() != null ? request.contentType() : pl.pjatk.alertwip.model.MatchType.CONTAINS);
        rule.setOriginPattern(request.originPattern());
        rule.setOriginMatchType(request.originMatchType() != null ? request.originMatchType() : pl.pjatk.alertwip.model.MatchType.CONTAINS);
        rule.setSubjectPattern(request.subjectPattern());
        rule.setSubjectMatchType(request.subjectMatchType() != null ? request.subjectMatchType() : pl.pjatk.alertwip.model.MatchType.CONTAINS);
        rule.setMinSeverity(request.minSeverity());
        rule.setPlaySound(request.playSound());

        AlertRule savedRule = alertRuleRepository.save(rule);
        return ResponseEntity.ok(savedRule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRule(@PathVariable Long id, @RequestBody AlertRuleRequestDTO request){

    }

    // 3. Usuwanie reguły
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRule(@PathVariable Long id) {
        if (!alertRuleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        alertRuleRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}