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
public class UserBanEmailNotificationListener implements UserBanListener {

    private final EmailService emailService;

    @Override
    @Async // Ensure email sending doesn't block the main thread
    public void onUserBan(UserBanWriteEvent event) {
        User user = event.getUser();
        if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
            log.warn("Cannot send ban email. User data is incomplete.");
            return;
        }

        log.info("Listener received UserBanWriteEvent for user: {}", user.getUsername());
        try {
            // Use the existing sendBanNotification method from EmailService
            emailService.sendBanNotification(user.getEmail(), user.getUsername(), event.getReason());
            log.info("Successfully queued ban notification email for user {} to {}", user.getId(), user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send ban notification email for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }
}