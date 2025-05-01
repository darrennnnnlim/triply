package com.example.triply.core.auth.service.impl;

import org.springframework.test.util.ReflectionTestUtils;
import com.example.triply.common.exception.*;
import com.example.triply.common.filter.JwtAuthenticationFilter;
import com.example.triply.core.admin.entity.UserStatus;
import com.example.triply.core.admin.repository.UserStatusRepository;
import com.example.triply.core.auth.dto.*;
import com.example.triply.core.auth.entity.RefreshToken;
import com.example.triply.core.auth.entity.Role;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.notification.UserRegistrationWritePublisher;
import com.example.triply.core.auth.repository.RoleRepository;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.auth.service.JwtService;
import com.example.triply.core.auth.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    @Mock
    private UserRegistrationWritePublisher userRegistrationWritePublisher;

    @BeforeEach
    void setup() {
        authService = new AuthServiceImpl(
                userRepository, roleRepository, passwordEncoder,
                userStatusRepository, authenticationManager, jwtService,
                refreshTokenService, jwtAuthenticationFilter, userRegistrationWritePublisher);

        ReflectionTestUtils.setField(authService, "accessTokenCookieExpiry", 3600);
        ReflectionTestUtils.setField(authService, "refreshTokenCookieExpiry", 7200);
    }


    @Test
    void login_success() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        User user = new User();
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setUsername("testuser");
        user.setRole(role);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("someRandomToken");
        refreshToken.setUser(user); // user must be a mocked or real User object
        refreshToken.setExpiryDate(new Date());
        refreshToken.setRevoked(false);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(any(), anySet())).thenReturn("accessToken");
        when(refreshTokenService.createRefreshToken(any())).thenReturn(refreshToken);

        AuthDTO result = authService.login(authRequest, response);

        assertEquals("testuser", result.getUsername());
        assertEquals("ROLE_USER", result.getRole());
        verify(response, times(2)).addCookie(any());
    }

    @Test
    void login_userNotFound() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("nouser");
        authRequest.setPassword("password");

        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(authRequest, response));
    }

    @Test
    void register_success() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userStatusRepository.findByStatus("ACTIVE")).thenReturn(Optional.of(new UserStatus()));

        AuthDTO result = authService.register(registerRequest);

        assertEquals("newuser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_usernameAlreadyExists() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("existinguser");
        registerRequest.setPassword("password");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(new User()));

        assertThrows(UsernameAlreadyExistException.class, () -> authService.register(registerRequest));
    }

    @Test
    void refreshToken_success() {
        User user = new User();
        user.setUsername("testuser");
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRole(role);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("someRandomToken");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(new Date());
        refreshToken.setRevoked(false);

        when(jwtAuthenticationFilter.extractRefreshTokenFromCookie(any())).thenReturn("refreshToken");
        when(jwtService.extractUsername("refreshToken", true)).thenReturn("testuser");
        when(jwtService.isTokenValid("refreshToken", "testuser", true)).thenReturn(true);
        when(refreshTokenService.getValidRefreshToken("refreshToken")).thenReturn(Optional.of(refreshToken));
        when(jwtService.generateAccessToken(eq("testuser"), anySet())).thenReturn("newAccessToken");

        TokenDTO result = authService.refreshToken(request, response);

        assertEquals("newAccessToken", result.getAccessToken());
    }

    @Test
    void refreshToken_missingToken() {
        when(jwtAuthenticationFilter.extractRefreshTokenFromCookie(any())).thenReturn(null);
        assertThrows(TokenException.class, () -> authService.refreshToken(request, response));
    }

    @Test
    void checkSession_loggedIn() {
        when(jwtAuthenticationFilter.extractAccessTokenFromCookie(request)).thenReturn("accessToken");
        when(jwtService.extractUsername("accessToken", false)).thenReturn("testuser");
        when(jwtService.isTokenValid("accessToken", "testuser", false)).thenReturn(true);
        User user = new User();
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRole(role);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        CheckSessionDTO result = authService.checkSession(request, response);

        assertTrue(result.isLoggedIn());
        assertEquals("testuser", result.getUsername());
        assertEquals("ROLE_USER", result.getRole());
        assertEquals("accessToken", result.getAccessToken());
    }

    @Test
    void checkSession_notLoggedIn() {
        when(jwtAuthenticationFilter.extractAccessTokenFromCookie(request)).thenReturn(null);

        CheckSessionDTO result = authService.checkSession(request, response);

        assertFalse(result.isLoggedIn());
    }

    @Test
    void logout_success() {
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken("refreshToken");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refreshToken");
        refreshToken.setUser(new User()); // user must be a mocked or real User object
        refreshToken.setExpiryDate(new Date());
        refreshToken.setRevoked(false);

        when(refreshTokenService.getValidRefreshToken("refreshToken")).thenReturn(Optional.of(refreshToken));

        String result = authService.logout(refreshRequest, response);

        assertEquals("Logged out successfully", result);
        verify(response, times(2)).addCookie(any());
    }

    @Test
    void resetPassword_success() {
        User user = new User();
        user.setPassword("encodedOldPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword", "encodedOldPassword")).thenReturn(false);

        boolean result = authService.resetPassword("testuser", "oldPassword", "newPassword");

        assertTrue(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void resetPassword_invalidCurrentPassword() {
        User user = new User();
        user.setPassword("encodedOldPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(false);

        assertThrows(InvalidCurrentPasswordException.class, () -> authService.resetPassword("testuser", "oldPassword", "newPassword"));
    }

    @Test
    void resetPassword_sameNewPassword() {
        User user = new User();
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword", "encodedPassword")).thenReturn(true);

        assertThrows(SameNewPasswordException.class, () -> authService.resetPassword("testuser", "oldPassword", "newPassword"));
    }
}
