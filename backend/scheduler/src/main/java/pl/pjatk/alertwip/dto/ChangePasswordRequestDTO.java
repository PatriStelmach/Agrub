package pl.pjatk.alertwip.dto;

public record ChangePasswordRequestDTO(
        String oldPassword,
        String newPassword
) {}