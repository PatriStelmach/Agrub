package pl.pjatk.alertwip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.GroupResponseDTO;
import pl.pjatk.alertwip.dto.UserResponseDTO;
import pl.pjatk.alertwip.model.User;
import pl.pjatk.alertwip.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
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

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserResponseDTO> editUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje"));

        user.setFirstname(userDetails.getFirstname());
        user.setSurname(userDetails.getSurname());
        user.setRole(userDetails.getRole());
        user.setActive(userDetails.isActive());

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
}