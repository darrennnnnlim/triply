package com.example.triply.core.auth.service.impl;

import com.example.triply.core.auth.entity.RefreshToken;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.RefreshTokenRepository;
import com.example.triply.core.auth.service.JwtService;
import com.example.triply.core.auth.service.RefreshTokenService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public RefreshToken createRefreshToken(User user) {
        String token = jwtService.generateRefreshToken(user.getUsername());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> getValidRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token).filter(t -> !t.isRevoked() && t.getExpiryDate().after(new Date()));
    }

    public void revokeToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
