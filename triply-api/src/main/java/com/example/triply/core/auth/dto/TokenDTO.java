package com.example.triply.core.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
    private String message;
}
