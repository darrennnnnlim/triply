package com.example.triply.core.auth.service.impl;

import com.example.triply.core.auth.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "issuer", "testIssuer");
        ReflectionTestUtils.setField(jwtService, "secretKey", "01234567890123456789012345678901"); // 32 bytes
        ReflectionTestUtils.setField(jwtService, "refreshSecretKey", "abcdefghijabcdefghijabcdefghij12"); // 32 bytes
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiry", 3600000); // 1 hour
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiry", 7200000L); // 2 hours
    }

    @Test
    void generateAccessToken_and_extractUsername_success() {
        Role role = new Role();
        role.setName("ROLE_USER");

        String accessToken = jwtService.generateAccessToken("testuser", Set.of(role));
        assertNotNull(accessToken);

        String username = jwtService.extractUsername(accessToken, false);
        assertEquals("testuser", username);
    }

    @Test
    void generateRefreshToken_and_extractUsername_success() {
        String refreshToken = jwtService.generateRefreshToken("testuser");
        assertNotNull(refreshToken);

        String username = jwtService.extractUsername(refreshToken, true);
        assertEquals("testuser", username);
    }

    @Test
    void isTokenValid_success() {
        Role role = new Role();
        role.setName("ROLE_USER");

        String token = jwtService.generateAccessToken("testuser", Set.of(role));

        boolean valid = jwtService.isTokenValid(token, "testuser", false);
        assertTrue(valid);
    }

    @Test
    void isTokenValid_invalidUsername() {
        Role role = new Role();
        role.setName("ROLE_USER");

        String token = jwtService.generateAccessToken("testuser", Set.of(role));

        boolean valid = jwtService.isTokenValid(token, "wronguser", false);
        assertFalse(valid);
    }

    @Test
    void extractRoles_success() {
        Role role1 = new Role();
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setName("ROLE_ADMIN");

        String token = jwtService.generateAccessToken("testuser", Set.of(role1, role2));

        List<String> roles = jwtService.extractRoles(token);

        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    @Test
    void extractUsernameFromRequest_success() {
        Role role = new Role();
        role.setName("ROLE_USER");

        String token = jwtService.generateAccessToken("testuser", Set.of(role));

        Cookie[] cookies = { new Cookie("accessToken", token) };

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(cookies);

        String username = jwtService.extractUsernameFromRequest(request);
        assertEquals("testuser", username);
    }

    @Test
    void extractUsernameFromRequest_noCookies() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        String username = jwtService.extractUsernameFromRequest(request);
        assertNull(username);
    }

    @Test
    void extractUsernameFromRequest_noAccessTokenCookie() {
        Cookie[] cookies = { new Cookie("otherCookie", "value") };

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(cookies);

        String username = jwtService.extractUsernameFromRequest(request);
        assertNull(username);
    }
}