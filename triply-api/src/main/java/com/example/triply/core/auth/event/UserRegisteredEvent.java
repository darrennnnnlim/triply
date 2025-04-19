package com.example.triply.core.auth.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {

    private final String email;
    private final String username;

    public UserRegisteredEvent(Object source, String email, String username) {
        super(source);
        this.email = email;
        this.username = username;
    }
}