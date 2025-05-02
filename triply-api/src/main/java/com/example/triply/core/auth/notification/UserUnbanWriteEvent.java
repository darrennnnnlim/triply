package com.example.triply.core.auth.notification;

import com.example.triply.core.auth.entity.User;
import lombok.Getter;

@Getter
public class UserUnbanWriteEvent {

    private final Object source;
    private final User user;

    public UserUnbanWriteEvent(Object source, User user) {
        this.source = source;
        this.user = user;
    }
}