package com.example.triply.core.auth.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserBanWritePublisherImpl implements UserBanWritePublisher {

    private final List<UserBanListener> listeners = new ArrayList<>();

    // Autowire all beans that implement UserBanListener
    @Autowired
    public UserBanWritePublisherImpl(List<UserBanListener> configuredListeners) {
        if (configuredListeners != null) {
            this.listeners.addAll(configuredListeners);
            log.info("Initialized UserBanWritePublisher with {} listeners.", configuredListeners.size());
        } else {
            log.warn("No UserBanListener beans found during initialization.");
        }
    }

    @Override
    public void publish(UserBanWriteEvent event) {
        log.info("Publishing UserBanWriteEvent for user: {}", event.getUser().getUsername());
        for (UserBanListener listener : listeners) {
            try {
                listener.onUserBan(event);
            } catch (Exception e) {
                log.error("Error notifying listener {} about user ban: {}", listener.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public void addListener(UserBanListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            log.info("Added listener: {}", listener.getClass().getSimpleName());
        }
    }

    @Override
    public void removeListener(UserBanListener listener) {
        if (listener != null && listeners.remove(listener)) {
            log.info("Removed listener: {}", listener.getClass().getSimpleName());
        }
    }
}