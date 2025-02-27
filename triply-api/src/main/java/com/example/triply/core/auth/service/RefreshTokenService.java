package com.example.triply.core.auth.service;

import com.example.triply.core.auth.entity.RefreshToken;
import com.example.triply.core.auth.entity.User;

import java.util.Optional;

public interface RefreshTokenService {

    public RefreshToken createRefreshToken(User user);

    public Optional<RefreshToken> getValidRefreshToken(String token);

    public void revokeToken(RefreshToken refreshToken);

}
