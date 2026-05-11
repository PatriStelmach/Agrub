package pl.pjatk.alertwip.dto;

import java.util.List;

public record UserResponseDTO(
        Long id,
        String email,
        String firstname,
        String surname,
        String role,
        boolean active,
        List<String> groups
) {}