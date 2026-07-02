package pl.pjatk.alertwip.dto;

import pl.pjatk.alertwip.model.Role;

import java.util.List;

public record UserSummaryDTO(
        Long id,
        String email,
        String firstname,
        String surname,
        Role role
) { }
