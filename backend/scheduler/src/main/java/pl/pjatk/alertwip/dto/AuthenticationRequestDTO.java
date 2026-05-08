package pl.pjatk.alertwip.dto;

public record AuthenticationRequestDTO(
        String email,
        String password
) {}