package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.GroupResponseDTO;
import pl.pjatk.alertwip.dto.UserResponseDTO;
import pl.pjatk.alertwip.model.ProblemAction;
import pl.pjatk.alertwip.model.User;
import pl.pjatk.alertwip.repository.UserRepository;
import pl.pjatk.alertwip.service.AlertActionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlertActionService alertActionService;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AlertActionService alertActionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.alertActionService = alertActionService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(mapToDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserResponseDTO> addUser(@RequestBody User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(mapToDTO(savedUser));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserResponseDTO> editUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje"));

        user.setFirstname(userDetails.getFirstname());
        user.setSurname(userDetails.getSurname());
        user.setRole(userDetails.getRole());
        user.setActive(userDetails.isActive());
        user.setGroups(userDetails.getGroups());
        user.setAutoLogoutMinutes(userDetails.getAutoLogoutMinutes());

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(mapToDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private UserResponseDTO mapToDTO(User user) {
        List<GroupResponseDTO> groups = user.getGroups().stream()
                .map(group -> new GroupResponseDTO(group.getId(), group.getName()))
                .collect(Collectors.toList());

        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getSurname(),
                user.getRole().name(),
                user.isActive(),
                groups
        );
    }

    @GetMapping("/me/settings")
    public ResponseEntity<?> getMySettings(org.springframework.security.core.Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> settings = new HashMap<>();
        settings.put("autoLogoutMinutes", user.getAutoLogoutMinutes() != null ? user.getAutoLogoutMinutes() : 480);
        settings.put("lastPasswordChangeDate", user.getLastPasswordChangeDate());

        return ResponseEntity.ok(settings);
    }

    @PatchMapping("/me/auto-logout")
    public ResponseEntity<?> updateAutoLogout(@RequestParam Integer minutes, org.springframework.security.core.Authentication authentication) {
        if (minutes == null || minutes < 1) {
            return ResponseEntity.badRequest().body(Map.of("error", "Minimalny czas to 1 minuta"));
        }
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAutoLogoutMinutes(minutes);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Zaktualizowano czas wylogowania"));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<?> changePassword(@RequestBody pl.pjatk.alertwip.dto.ChangePasswordRequestDTO request, org.springframework.security.core.Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        if ("EXTERNAL_AD_AUTH".equals(user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Konta AD nie mogą zmieniać hasła z poziomu aplikacji."));
        }

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Stare hasło jest nieprawidłowe."));
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setLastPasswordChangeDate(java.time.LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Hasło zostało pomyślnie zmienione."));
    }

    @GetMapping("/{id}/actions")
    public ResponseEntity<org.springframework.data.domain.Page<pl.pjatk.alertwip.dto.ProblemActionDTO>> getAllActionsByUsers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @ModelAttribute pl.pjatk.alertwip.dto.AlertHistoryFiltersDTO filters
    ) {
        // 1. Wyciągamy użytkownika
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o ID: " + id));

        String authorIdentifier = user.getUsername();

        // 2. Przygotowanie paginacji
        org.springframework.data.domain.Sort.Direction direction = org.springframework.data.domain.Sort.Direction.fromString(sortDir.toUpperCase());
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(direction, sortBy));

        // 3. Pobranie z serwisu strony z ENCJI (zastosowano filtry)
        org.springframework.data.domain.Page<pl.pjatk.alertwip.model.ProblemAction> actionsPage =
                alertActionService.getFilteredActionsByAuthor(authorIdentifier, filters, pageable);

        // 4. MAPOWANIE: Przepisujemy encje na lekkie DTO (tutaj wyciągamy subject!)
        org.springframework.data.domain.Page<pl.pjatk.alertwip.dto.ProblemActionDTO> dtoPage = actionsPage.map(action ->
                new pl.pjatk.alertwip.dto.ProblemActionDTO(
                        action.getId(),
                        action.getProblem().getId(),
                        action.getProblem().getSubject(), // <--- Temat wyciągnięty z relacji
                        action.getAuthor(),
                        action.getMessage(),
                        action.getCreatedAt(),
                        action.getProblem().getClosedAt(), // <--- Data zamknięcia wyciągnięta z relacji
                        action.getAckUpdate(),
                        action.getPreviousSeverity(),
                        action.getNewSeverity(),
                        action.getSyncStatus()
                )
        );

        // Zwracamy czyste, bezpieczne i zmapowane dane
        return ResponseEntity.ok(dtoPage);
    }
}