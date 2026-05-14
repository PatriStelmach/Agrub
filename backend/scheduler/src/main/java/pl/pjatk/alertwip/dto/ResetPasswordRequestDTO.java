package pl.pjatk.alertwip.dto;

public record ResetPasswordRequestDTO(String token, String newPassword) {}