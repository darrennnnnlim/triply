package com.example.triply.core.auth.notification;

public interface UserUnbanWritePublisher {
    void publish(UserUnbanWriteEvent event);
    void addListener(UserUnbanListener listener);
    void removeListener(UserUnbanListener listener);
}