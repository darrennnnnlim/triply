package com.example.triply.core.auth.dto;

import lombok.Value;

@Value
public class AuthResponse {

    private String accessToken;
    private String refreshToken;

}
