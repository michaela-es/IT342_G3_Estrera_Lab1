package com.example.estrera.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            String verificationUrl = baseUrl + "/api/auth/verify-email?token=" + token;

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .button { 
                            background-color: #4CAF50; 
                            color: white; 
                            padding: 12px 24px; 
                            text-decoration: none; 
                            border-radius: 4px; 
                            display: inline-block;
                            margin: 20px 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h2>Email Verification</h2>
                        <p>Please click the button below to verify your email address:</p>
                        <a href="%s" class="button">Verify Email</a>
                        <p>Or copy and paste this link in your browser:</p>
                        <p>%s</p>
                        <p>This link will expire in 24 hours.</p>
                    </div>
                </body>
                </html>
                """.formatted(verificationUrl, verificationUrl);

            helper.setText(htmlContent, true);
            helper.setTo(toEmail);
            helper.setSubject("Verify Your Email Address");
            helper.setFrom(fromEmail);

            mailSender.send(mimeMessage);
            log.info("Verification email sent to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            // Fallback to simple email
            sendSimpleVerificationEmail(toEmail, token);
        }
    }

    private void sendSimpleVerificationEmail(String toEmail, String token) {
        try {
            String verificationUrl = baseUrl + "/api/auth/verify-email?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Verify Your Email Address");
            message.setText("Please click the link to verify your email: " + verificationUrl);
            message.setFrom(fromEmail);

            mailSender.send(message);
            log.info("Simple verification email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send simple verification email: {}", e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            String resetUrl = baseUrl + "/api/auth/reset-password?token=" + token;

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <body>
                    <h2>Password Reset</h2>
                    <p>Click the link below to reset your password:</p>
                    <a href="%s">Reset Password</a>
                    <p>This link will expire in 1 hour.</p>
                </body>
                </html>
                """.formatted(resetUrl);

            helper.setText(htmlContent, true);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");
            helper.setFrom(fromEmail);

            mailSender.send(mimeMessage);
            log.info("Password reset email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
        }
    }
}