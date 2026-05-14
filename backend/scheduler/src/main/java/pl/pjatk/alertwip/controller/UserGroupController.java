package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.UserGroupStatsDTO;
import pl.pjatk.alertwip.model.User;
import pl.pjatk.alertwip.model.UserGroup;
import pl.pjatk.alertwip.repository.AlertRuleRepository;
import pl.pjatk.alertwip.repository.UserGroupRepository;
import pl.pjatk.alertwip.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class UserGroupController {

    private final UserGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final AlertRuleRepository alertRuleRepository;

    public UserGroupController(UserGroupRepository groupRepository,
                               UserRepository userRepository,
                               AlertRuleRepository alertRuleRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.alertRuleRepository = alertRuleRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserGroup>> getAllGroups() {
        return ResponseEntity.ok(groupRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserGroup> createGroup(@RequestBody UserGroup group) {
        return ResponseEntity.ok(groupRepository.save(group));
    }

    //Pobieranie wszystkich grup razem z liczbą userów do nich przypisanych i liczbą zasad do nich przypisanych
    @GetMapping("/stats")
    public ResponseEntity<List<UserGroupStatsDTO>> getAllGroupsWithStats() {
        List<UserGroup> groups = groupRepository.findAll();

        List<UserGroupStatsDTO> statsList = groups.stream().map(group -> {
            long userCount = userRepository.countByGroupsId(group.getId());
            long ruleCount = alertRuleRepository.countByUserGroupId(group.getId());

            return new UserGroupStatsDTO(group.getId(), group.getName(), userCount, ruleCount);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(statsList);
    }

    //endpoint na pobranie wszystkich userów oraz wszystkich zasad dla konkretnej grupy
    @GetMapping("/{id}/details")
    public ResponseEntity<pl.pjatk.alertwip.dto.UserGroupDetailsDTO> getGroupDetails(@PathVariable Long id) {

        // 1. Sprawdzamy czy grupa w ogóle istnieje
        UserGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono grupy o id: " + id));

        // 2. Pobieramy dane powiązane
        List<User> users = userRepository.findAllByGroupsId(id);
        List<pl.pjatk.alertwip.model.AlertRule> rules = alertRuleRepository.findAllByUserGroupId(id);

        // 3. Mapujemy listę Użytkowników
        List<pl.pjatk.alertwip.dto.UserSummaryDTO> userDTOs = users.stream()
                .map(u -> new pl.pjatk.alertwip.dto.UserSummaryDTO(
                        u.getId(),
                        u.getUsername(),
                        u.getFirstname(),
                        u.getSurname()
                ))
                .collect(java.util.stream.Collectors.toList());

        // 4. Mapujemy listę Zasad Alertowania
        List<pl.pjatk.alertwip.dto.AlertRuleRequestDTO> ruleDTOs = rules.stream()
                .map(r -> new pl.pjatk.alertwip.dto.AlertRuleRequestDTO(
                        r.getId(),
                        r.getSourcePattern(),
                        r.getSourceType(),
                        r.getContentPattern(),
                        r.getContentType(),
                        r.getSubjectPattern(),
                        r.getSubjectMatchType(),
                        r.getOriginPattern(),
                        r.getOriginMatchType(),
                        r.getMinSeverity(),
                        r.isPlaySound()
                ))
                .collect(java.util.stream.Collectors.toList());

        // 5. Składamy w finalny obiekt JSON
        pl.pjatk.alertwip.dto.UserGroupDetailsDTO response = new pl.pjatk.alertwip.dto.UserGroupDetailsDTO(
                group.getId(),
                group.getName(),
                userDTOs,
                ruleDTOs
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{groupId}/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<User> addUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono grupy o id: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o id: " + userId));

        if (!user.getGroups().contains(group)) {
            user.getGroups().add(group);
            userRepository.save(user);
        }
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{groupId}/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<User> removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono grupy o id: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o id: " + userId));

        if (user.getGroups().contains(group)) {
            user.getGroups().remove(group);
            userRepository.save(user);
        }
        return ResponseEntity.ok(user);
    }
}