package com.example.triply.core.auth.notification;

import com.example.triply.common.service.EmailService;
import com.example.triply.core.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationEmailNotificationListener implements UserRegistrationListener {

    private final EmailService emailService;

    @Override
    @Async // Ensure email sending doesn't block the main thread
    public void onUserRegistration(UserRegistrationWriteEvent event) {
        User user = event.getUser();
        if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
            log.warn("Cannot send registration email. User data is incomplete.");
            return;
        }

        log.info("Listener received UserRegistrationWriteEvent for user: {}", user.getUsername());
        try {
            String subject = "Welcome to Triply!";
            String bodyTemplate = "Hi %s,\n\nWelcome to Triply! We're excited to have you on board.\n\nThanks,\nThe Triply Team";
            String body = String.format(bodyTemplate, user.getUsername());

            log.info("Attempting to send registration email to user {} ({})", user.getId(), user.getEmail());
            emailService.sendEmail(user.getEmail(), subject, body);
            log.info("Successfully queued registration notification email for user {} to {}", user.getId(), user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send registration notification email for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }
}