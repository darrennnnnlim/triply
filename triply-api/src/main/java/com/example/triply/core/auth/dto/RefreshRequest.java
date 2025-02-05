package com.example.triply.core.auth.dto;

import lombok.Data;

@Data
public class RefreshRequest {

    private String refreshToken;
    private String role;

}
