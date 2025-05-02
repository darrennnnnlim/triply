package com.example.triply.core.auth.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class UserUnbanWritePublisherImpl implements UserUnbanWritePublisher {

    private final List<UserUnbanListener> listeners = new CopyOnWriteArrayList<>(); // Use thread-safe list

    // Autowire all beans that implement UserUnbanListener
    @Autowired
    public UserUnbanWritePublisherImpl(List<UserUnbanListener> configuredListeners) {
        if (configuredListeners != null) {
            this.listeners.addAll(configuredListeners);
            log.info("Initialized UserUnbanWritePublisher with {} listeners.", configuredListeners.size());
        } else {
            log.warn("No UserUnbanListener beans found during initialization.");
        }
    }

    @Override
    public void publish(UserUnbanWriteEvent event) {
        log.info("Publishing UserUnbanWriteEvent for user: {}", event.getUser().getUsername());
        for (UserUnbanListener listener : listeners) {
            try {
                // Consider running listeners asynchronously if they perform long operations
                listener.onUserUnban(event);
            } catch (Exception e) {
                log.error("Error notifying listener {} about user unban: {}", listener.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public void addListener(UserUnbanListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            log.info("Added listener: {}", listener.getClass().getSimpleName());
        }
    }

    @Override
    public void removeListener(UserUnbanListener listener) {
        if (listener != null && listeners.remove(listener)) {
            log.info("Removed listener: {}", listener.getClass().getSimpleName());
        }
    }
}