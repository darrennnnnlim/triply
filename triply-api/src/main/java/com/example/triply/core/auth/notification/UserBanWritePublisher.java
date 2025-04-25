package com.example.triply.core.auth.notification;

public interface UserBanWritePublisher {
    void publish(UserBanWriteEvent event);
    void addListener(UserBanListener listener);
    void removeListener(UserBanListener listener);
}