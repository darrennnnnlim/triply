package com.example.triply.core.auth.service;

import com.example.triply.core.auth.entity.Role;

import java.util.List;
import java.util.Set;

public interface JwtService {

    public String generateAccessToken(String username, Set<Role> roles);

    public String generateRefreshToken(String username);

    public String extractUsername(String token, boolean isRefresh);

    public boolean isTokenValid(String token, String username, boolean isRefresh);

    public List<String> extractRoles(String token);
}
