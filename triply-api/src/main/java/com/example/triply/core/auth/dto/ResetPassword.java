package com.example.triply.core.auth.dto;

import lombok.Data;

@Data
public class ResetPassword {
    private String currentPassword;
    private String newPassword;
}