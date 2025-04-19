package com.example.triply.core.auth.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserBannedEvent extends ApplicationEvent {

    private final String email;
    private final String username;
    private final String reason;

    public UserBannedEvent(Object source, String email, String username, String reason) {
        super(source);
        this.email = email;
        this.username = username;
        this.reason = reason;
    }
}