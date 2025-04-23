package com.example.triply.core.auth.service;

import com.example.triply.core.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    public AuthDTO login(AuthRequest authRequest, HttpServletResponse response);
    public AuthDTO register(RegisterRequest registerRequest);
    public TokenDTO refreshToken(HttpServletRequest request, HttpServletResponse response);
    public CheckSessionDTO checkSession(HttpServletRequest request, HttpServletResponse response);
    public String logout(RefreshRequest refreshRequest, HttpServletResponse response);
    public boolean resetPassword(String username, String currentPassword, String newPassword);
}
