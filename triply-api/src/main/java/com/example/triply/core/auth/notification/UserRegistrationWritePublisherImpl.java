package com.example.triply.core.auth.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserRegistrationWritePublisherImpl implements UserRegistrationWritePublisher {

    private final List<UserRegistrationListener> listeners = new ArrayList<>();

    // Autowire all beans that implement UserRegistrationListener
    @Autowired
    public UserRegistrationWritePublisherImpl(List<UserRegistrationListener> configuredListeners) {
        if (configuredListeners != null) {
            this.listeners.addAll(configuredListeners);
            log.info("Initialized UserRegistrationWritePublisher with {} listeners.", configuredListeners.size());
        } else {
            log.warn("No UserRegistrationListener beans found during initialization.");
        }
    }

    @Override
    public void publish(UserRegistrationWriteEvent event) {
        log.info("Publishing UserRegistrationWriteEvent for user: {}", event.getUser().getUsername());
        for (UserRegistrationListener listener : listeners) {
            try {
                listener.onUserRegistration(event);
            } catch (Exception e) {
                log.error("Error notifying listener {} about user registration: {}", listener.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public void addListener(UserRegistrationListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            log.info("Added listener: {}", listener.getClass().getSimpleName());
        }
    }

    @Override
    public void removeListener(UserRegistrationListener listener) {
        if (listener != null && listeners.remove(listener)) {
            log.info("Removed listener: {}", listener.getClass().getSimpleName());
        }
    }
}