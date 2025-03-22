package com.academiaconnect.auth.authservice.infrastructure.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Async
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> templateVariables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariables(templateVariables);
            String htmlContent = templateEngine.process(templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@academiaconnect.com");

            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendVerificationEmail(String to, String token) {
        String verificationUrl = authServiceUrl + "/api/auth/verify-email?token=" + token;
        Map<String, Object> templateVariables = Map.of(
                "name", to.split("@")[0],
                "verificationUrl", verificationUrl,
                "frontendUrl", frontendUrl
        );
        sendEmail(to, "Verify Your Email", "verification-email", templateVariables);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        Map<String, Object> templateVariables = Map.of(
                "name", to.split("@")[0],
                "resetUrl", resetUrl,
                "frontendUrl", frontendUrl
        );
        sendEmail(to, "Reset Your Password", "reset-password-email", templateVariables);
    }
}