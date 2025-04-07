package com.example.triply.common.service.impl;

import com.example.triply.common.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    
    @Override
    public void sendRegistrationEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("triplymain@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Welcome to Triply");
        message.setText(String.format(
            "Hello %s,\n\nThank you for registering with Triply!\n\n" +
            "We're excited to have you on board.\n\n" +
            "Best regards,\nThe Triply Team",
            username
        ));
        mailSender.send(message);
    }
    @Override
    public void sendBanNotification(String toEmail, String username, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("triplymain@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Your Triply Account Status");
        message.setText(String.format(
            "Hello %s,\n\n" +
            "Your Triply account has been %s.\n" +
            "Reason: %s\n\n" +
            "If you believe this was done in error, please contact support.\n\n" +
            "Best regards,\nThe Triply Team",
            username,
            reason != null && !reason.isEmpty() ? "banned" : "suspended",
            reason != null && !reason.isEmpty() ? reason : "No reason provided"
        ));
        mailSender.send(message);
    }
}