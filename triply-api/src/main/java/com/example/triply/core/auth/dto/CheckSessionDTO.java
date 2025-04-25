package com.example.triply.core.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckSessionDTO {
    private boolean isLoggedIn;
    private String username;
    private String role;
    private String accessToken;
    private String mesasge;
}
