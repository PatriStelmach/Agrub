package pl.pjatk.alertwip.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    private final SystemSettingService settingService;

    public EmailService(SystemSettingService settingService) {
        this.settingService = settingService;
    }

    private JavaMailSenderImpl getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(settingService.getValue("smtp_host", "smtp.example.com"));

        int port;
        try {
            port = Integer.parseInt(settingService.getValue("smtp_port", "587"));
        } catch (NumberFormatException e) {
            port = 587;
        }
        mailSender.setPort(port);

        mailSender.setUsername(settingService.getValue("smtp_user", ""));
        mailSender.setPassword(settingService.getValue("smtp_password_SECRET", ""));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        return mailSender;
    }

    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        if (!settingService.getBoolean("smtp_enabled", false)) {
            System.out.println("[EMAIL] Wysyłka zablokowana (smtp_enabled = false). Przerwano wysyłkę do: " + to);
            return;
        }

        try {
            JavaMailSenderImpl mailSender = getJavaMailSender();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(settingService.getValue("smtp_user", "alerts@system.local"));
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            System.out.println("[EMAIL] Pomyślnie wysłano wiadomość do: " + to);

        } catch (Exception e) {
            System.err.println("[EMAIL] Błąd podczas wysyłania e-maila do " + to + ": " + e.getMessage());
        }
    }
}