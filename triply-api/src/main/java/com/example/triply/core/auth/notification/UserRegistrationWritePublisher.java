package com.example.triply.core.auth.notification;

public interface UserRegistrationWritePublisher {
    void publish(UserRegistrationWriteEvent event);
    void addListener(UserRegistrationListener listener);
    void removeListener(UserRegistrationListener listener);
}