package com.example.triply.core.auth.service;

public interface JwtService {

    public String generateAccessToken(String username, String role);

    public String generateRefreshToken(String username);

    public String extractUsername(String token, boolean isRefresh);

    public boolean isTokenValid(String token, String username, boolean isRefresh);
}
