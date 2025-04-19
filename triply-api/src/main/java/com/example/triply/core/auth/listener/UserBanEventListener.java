package com.example.triply.core.auth.listener;

import com.example.triply.common.service.EmailService;
import com.example.triply.core.auth.event.UserBannedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanEventListener {

    private final EmailService emailService;

    @Async // Run email sending asynchronously
    @EventListener
    public void handleUserBannedEvent(UserBannedEvent event) {
        log.info("Received UserBannedEvent for email: {}", event.getEmail());
        try {
            emailService.sendBanNotification(event.getEmail(), event.getUsername(), event.getReason());
            log.info("Successfully sent ban notification email to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send ban notification email to {}: {}", event.getEmail(), e.getMessage(), e);
            // Consider adding retry logic or dead-letter queue mechanism here
        }
    }
}