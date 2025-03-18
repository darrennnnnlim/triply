package com.example.triply.core.auth.resource;

import com.example.triply.core.auth.dto.AuthRequest;
import com.example.triply.core.auth.dto.AuthResponse;
import com.example.triply.core.auth.dto.RefreshRequest;
import com.example.triply.core.auth.dto.RegisterRequest;
import com.example.triply.core.auth.entity.RefreshToken;
import com.example.triply.core.auth.entity.Role;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.RoleRepository;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.auth.service.JwtService;
import com.example.triply.core.auth.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthResourceTest {

    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private RefreshTokenService refreshTokenService;
    private AuthResource authResource;

    @BeforeEach
    public void setUp() {
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        refreshTokenService = mock(RefreshTokenService.class);

        authResource = new AuthResource(jwtService, authenticationManager, userRepository,
                roleRepository, passwordEncoder, refreshTokenService);

        // Inject cookie expiry values as expected by the controller.
        ReflectionTestUtils.setField(authResource, "accessTokenCookieExpiry", 3600);
        ReflectionTestUtils.setField(authResource, "refreshTokenCookieExpiry", 7200);
    }

    @Test
    public void testLoginTestEndpoint() {
        ResponseEntity<String> response = authResource.login();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Done", response.getBody());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // Arrange: Create an AuthRequest and set values.
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("john_doe");
        authRequest.setPassword("password");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Simulate authentication (no exception thrown) and find the user.
        User user = new User();
        user.setUsername("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L, "ROLE_USER"));

        // Setup JWT and refresh token generation.
        when(jwtService.generateAccessToken("john_doe", "USER")).thenReturn("access-token");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        // Act: Call the login endpoint.
        ResponseEntity<?> respEntity = authResource.login(authRequest, request, response);

        // Assert: Verify a 200 OK response with a "Login Successful" message.
        assertEquals(200, respEntity.getStatusCodeValue());
        assertTrue(((Map<?, ?>) respEntity.getBody()).containsKey("message"));
        assertEquals("Login Successful", ((Map<?, ?>) respEntity.getBody()).get("message"));

        // Verify that two cookies (access and refresh tokens) were added.
        verify(response, times(2)).addCookie(any(Cookie.class));
    }

    @Test
    public void testLoginInvalidCredentials() throws Exception {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("john_doe");
        authRequest.setPassword("wrong_password");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Simulate authentication failure by throwing an exception.
        doThrow(new RuntimeException("Auth failed"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act
        ResponseEntity<?> respEntity = authResource.login(authRequest, request, response);

        // Assert: Expect a 401 response with "Invalid credentials".
        assertEquals(401, respEntity.getStatusCodeValue());
        assertEquals("Invalid credentials", respEntity.getBody());
    }

    @Test
    public void testLoginUserNotFound() throws Exception {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("non_existent");
        authRequest.setPassword("password");
        authRequest.setRole("USER");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Simulate user not found.
        when(userRepository.findByUsername("non_existent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> respEntity = authResource.login(authRequest, request, response);

        // Assert: Expect a 401 response with "User not found".
        assertEquals(401, respEntity.getStatusCodeValue());
        assertEquals("User not found", respEntity.getBody());
    }

    @Test
    public void testRegisterConflict() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("existing_user");
        registerRequest.setPassword("password");

        // Simulate username already taken.
        when(userRepository.findByUsername("existing_user")).thenReturn(Optional.of(new User()));

        // Act
        ResponseEntity<?> response = authResource.register(registerRequest);

        // Assert: Expect a 409 CONFLICT response.
        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Username is already taken.", response.getBody());
    }

    @Test
    public void testRegisterSuccess() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("new_user");
        registerRequest.setPassword("password");

        // Simulate that the username is available.
        when(userRepository.findByUsername("new_user")).thenReturn(Optional.empty());
        // Simulate that ROLE_USER does not exist yet.
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());
        Role newRole = new Role();
        newRole.setName("ROLE_USER");
        when(roleRepository.save(any(Role.class))).thenReturn(newRole);
        // Simulate password encoding.
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Act
        ResponseEntity<?> response = authResource.register(registerRequest);

        // Assert: Expect a 201 CREATED response with a success message.
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("User registered successfully.", response.getBody());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRefreshMissingToken() {
        // Arrange
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Act: Call refresh with a null token.
        ResponseEntity<?> respEntity = authResource.refresh(null, response);

        // Assert: Expect a 403 FORBIDDEN response with a "Missing refresh token" message.
        assertEquals(403, respEntity.getStatusCodeValue());
        assertEquals("Missing refresh token", respEntity.getBody());
    }

    @Test
    public void testRefreshInvalidToken() {
        // Arrange
        HttpServletResponse response = mock(HttpServletResponse.class);
        String refreshTokenStr = "invalid-token";

        // Simulate JWT extraction and validation failure.
        when(jwtService.extractUsername(refreshTokenStr, true)).thenReturn("john_doe");
        when(jwtService.isTokenValid(refreshTokenStr, "john_doe", true)).thenReturn(false);

        // Act
        ResponseEntity<?> respEntity = authResource.refresh(refreshTokenStr, response);

        // Assert: Expect a 403 FORBIDDEN response with an "Invalid refresh token" message.
        assertEquals(403, respEntity.getStatusCodeValue());
        assertEquals("Invalid refresh token", respEntity.getBody());
    }

    @Test
    public void testRefreshInvalidOrExpiredRefreshToken() {
        // Arrange
        HttpServletResponse response = mock(HttpServletResponse.class);
        String refreshTokenStr = "valid-refresh-token";

        // Stub JWT service to simulate a valid refresh token signature/structure.
        when(jwtService.extractUsername(refreshTokenStr, true)).thenReturn("john_doe");
        when(jwtService.isTokenValid(refreshTokenStr, "john_doe", true)).thenReturn(true);

        // Stub the refreshTokenService to return an empty Optional, simulating an invalid or expired token.
        when(refreshTokenService.getValidRefreshToken(refreshTokenStr)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> respEntity = authResource.refresh(refreshTokenStr, response);

        // Assert
        assertEquals(403, respEntity.getStatusCodeValue());
        assertEquals("Invalid or expired refresh token", respEntity.getBody());

        // Verify that no new access token cookie was added.
        verify(response, never()).addCookie(any(Cookie.class));
    }


    @Test
    public void testRefreshSuccess() {
        // Arrange
        HttpServletResponse response = mock(HttpServletResponse.class);
        String refreshTokenStr = "valid-refresh-token";

        // Simulate token extraction and validation.
        when(jwtService.extractUsername(refreshTokenStr, true)).thenReturn("john_doe");
        when(jwtService.isTokenValid(refreshTokenStr, "john_doe", true)).thenReturn(true);

        // Simulate a valid refresh token existing.
        RefreshToken validRefreshToken = new RefreshToken();
        validRefreshToken.setToken(refreshTokenStr);
        when(refreshTokenService.getValidRefreshToken(refreshTokenStr)).thenReturn(Optional.of(validRefreshToken));

        // Simulate new access token generation.
        when(jwtService.generateAccessToken("john_doe", "USER")).thenReturn("new-access-token");

        // Act
        ResponseEntity<?> respEntity = authResource.refresh(refreshTokenStr, response);

        // Assert: Verify a 200 OK response with an AuthResponse.
        assertEquals(200, respEntity.getStatusCodeValue());
        assertTrue(respEntity.getBody() instanceof AuthResponse);
        AuthResponse authResponse = (AuthResponse) respEntity.getBody();
        assertEquals("new-access-token", authResponse.getAccessToken());
        assertEquals(refreshTokenStr, authResponse.getRefreshToken());
        // Verify that an access token cookie was added.
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    public void testLogout() {
        // Arrange
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken("some-refresh-token");

        // Simulate that a valid refresh token is found.
        RefreshToken validRefreshToken = new RefreshToken();
        validRefreshToken.setToken("some-refresh-token");
        when(refreshTokenService.getValidRefreshToken("some-refresh-token")).thenReturn(Optional.of(validRefreshToken));

        // Act
        ResponseEntity<?> response = authResource.logout(refreshRequest);

        // Assert: Verify a 200 OK response and that the token is revoked.
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logged out successfully", response.getBody());
        verify(refreshTokenService, times(1)).revokeToken(validRefreshToken);
    }
}
