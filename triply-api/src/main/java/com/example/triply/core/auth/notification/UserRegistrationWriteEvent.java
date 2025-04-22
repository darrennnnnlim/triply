package com.example.triply.core.auth.notification;

import com.example.triply.core.auth.entity.User;
import lombok.Getter;

@Getter
public class UserRegistrationWriteEvent {

    private final Object source;
    private final User user;

    public UserRegistrationWriteEvent(Object source, User user) {
        this.source = source;
        this.user = user;
    }
}