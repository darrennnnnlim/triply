package com.example.triply.core.auth.listener;

import com.example.triply.common.service.EmailService;
import com.example.triply.core.auth.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationEventListener {

    private final EmailService emailService;

    @Async // Run email sending asynchronously
    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent for email: {}", event.getEmail());
        try {
            emailService.sendRegistrationEmail(event.getEmail(), event.getUsername());
            log.info("Successfully sent registration email to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send registration email to {}: {}", event.getEmail(), e.getMessage(), e);
            // Consider adding retry logic or dead-letter queue mechanism here
        }
    }
}