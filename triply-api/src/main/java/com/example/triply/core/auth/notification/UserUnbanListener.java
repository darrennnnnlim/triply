package com.example.triply.core.auth.notification;

public interface UserUnbanListener {
    void onUserUnban(UserUnbanWriteEvent event);
}