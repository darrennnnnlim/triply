package com.example.triply.core.auth.notification;

import com.example.triply.core.auth.entity.User;
import lombok.Getter;

@Getter
public class UserBanWriteEvent {

    private final Object source;
    private final User user;
    private final String reason;

    public UserBanWriteEvent(Object source, User user, String reason) {
        this.source = source;
        this.user = user;
        this.reason = reason;
    }
}