package com.example.triply.core.auth.service.impl;

import com.example.triply.core.auth.entity.RefreshToken;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.RefreshTokenRepository;
import com.example.triply.core.auth.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceImplTest {

    private RefreshTokenServiceImpl refreshTokenService;
    private RefreshTokenRepository refreshTokenRepository;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        jwtService = mock(JwtService.class);
        refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository, jwtService);
    }

    @Test
    void createRefreshToken_success() {
        User user = new User();
        user.setUsername("testuser");

        when(jwtService.generateRefreshToken("testuser")).thenReturn("generatedToken");
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token = refreshTokenService.createRefreshToken(user);

        assertEquals("generatedToken", token.getToken());
        assertEquals(user, token.getUser());
        assertFalse(token.isRevoked());
        assertNotNull(token.getExpiryDate());
    }

    @Test
    void getValidRefreshToken_validToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + 100000));

        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> result = refreshTokenService.getValidRefreshToken("token");

        assertTrue(result.isPresent());
    }

    @Test
    void getValidRefreshToken_invalidToken_revoked() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRevoked(true);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + 100000));

        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> result = refreshTokenService.getValidRefreshToken("token");

        assertTrue(result.isEmpty());
    }

    @Test
    void getValidRefreshToken_invalidToken_expired() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() - 100000));

        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> result = refreshTokenService.getValidRefreshToken("token");

        assertTrue(result.isEmpty());
    }

    @Test
    void revokeToken_success() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRevoked(false);

        when(refreshTokenRepository.save(refreshToken)).thenReturn(refreshToken);

        refreshTokenService.revokeToken(refreshToken);

        assertTrue(refreshToken.isRevoked());
        verify(refreshTokenRepository).save(refreshToken);
    }
}