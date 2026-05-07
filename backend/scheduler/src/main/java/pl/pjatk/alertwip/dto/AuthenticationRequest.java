package pl.pjatk.alertwip.dto;

public record AuthenticationRequest(
        String email,
        String password
) {}