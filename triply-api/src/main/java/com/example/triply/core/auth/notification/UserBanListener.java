package com.example.triply.core.auth.notification;

public interface UserBanListener {
    void onUserBan(UserBanWriteEvent event);
}