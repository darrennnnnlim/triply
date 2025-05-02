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
public class UserUnbanEmailNotificationListener implements UserUnbanListener {

    private final EmailService emailService;

    @Override
    @Async // Ensure email sending doesn't block the main thread
    public void onUserUnban(UserUnbanWriteEvent event) {
        User user = event.getUser();
        if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
            log.warn("Cannot send unban email. User data is incomplete.");
            return;
        }

        log.info("Listener received UserUnbanWriteEvent for user: {}", user.getUsername());
        try {
            String subject = "Your Account Has Been Reinstated";
            String body = String.format("Dear %s,\n\nYour account on Triply has been reinstated. You can now log in and use our services again.\n\nWelcome back!\n\nBest regards,\nThe Triply Team", user.getUsername());

            // Use the generic sendEmail method
            emailService.sendEmail(user.getEmail(), subject, body);
            log.info("Successfully queued unban notification email for user {} to {}", user.getId(), user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send unban notification email for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }
}