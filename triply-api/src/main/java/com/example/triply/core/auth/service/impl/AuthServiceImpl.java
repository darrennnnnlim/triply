package com.example.triply.core.auth.service.impl;

// import com.example.triply.common.service.EmailService; // Removed this import

import com.example.triply.common.constants.CommonConstants;
import com.example.triply.common.exception.TokenException;
import com.example.triply.common.exception.UserNotFoundException;
import com.example.triply.common.exception.UsernameAlreadyExistException;
import com.example.triply.common.exception.InvalidCurrentPasswordException;
import com.example.triply.common.exception.SameNewPasswordException;
import com.example.triply.common.filter.JwtAuthenticationFilter;
import com.example.triply.core.admin.entity.UserStatus;
import com.example.triply.core.admin.repository.UserStatusRepository;
import com.example.triply.core.auth.dto.*;
import com.example.triply.core.auth.entity.RefreshToken;
import com.example.triply.core.auth.entity.Role;
import com.example.triply.core.auth.entity.User;
// import com.example.triply.core.auth.event.UserRegisteredEvent; // Removed Spring event import
import com.example.triply.core.auth.notification.UserRegistrationWriteEvent; // Added in-house event import
import com.example.triply.core.auth.notification.UserRegistrationWritePublisher; // Added in-house publisher import
import com.example.triply.core.auth.repository.RoleRepository;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.auth.service.AuthService;
import com.example.triply.core.auth.service.JwtService;
import com.example.triply.core.auth.service.RefreshTokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.ApplicationEventPublisher; // Removed Spring publisher import
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.access-token.cookie-expiry}")
    private int accessTokenCookieExpiry;

    @Value("${jwt.refresh-token.cookie-expiry}")
    private int refreshTokenCookieExpiry;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserStatusRepository userStatusRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
    // private final ApplicationEventPublisher applicationEventPublisher; // Removed Spring publisher field
    private final UserRegistrationWritePublisher userRegistrationWritePublisher; // Added in-house publisher field

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserStatusRepository userStatusRepository, @Lazy AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenService refreshTokenService, JwtAuthenticationFilter jwtAuthenticationFilter, UserRegistrationWritePublisher userRegistrationWritePublisher) { // Added in-house publisher
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userStatusRepository = userStatusRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        // this.applicationEventPublisher = applicationEventPublisher; // Removed assignment
        this.userRegistrationWritePublisher = userRegistrationWritePublisher; // Added assignment
    }

    @Override
    public AuthDTO login(AuthRequest authRequest, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        Optional<User> userOptional = userRepository.findByUsername(authRequest.getUsername());
        if (userOptional.isPresent()) {
            String accessToken = jwtService.generateAccessToken(authRequest.getUsername(), Set.of(userOptional.get().getRole()));
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userOptional.get());

            Cookie accessTokenCookie = new Cookie(CommonConstants.ACCESS_TOKEN, accessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(accessTokenCookieExpiry);

            Cookie refreshTokenCookie = new Cookie(CommonConstants.REFRESH_TOKEN, refreshToken.getToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(refreshTokenCookieExpiry);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            AuthDTO authDTO = new AuthDTO();
            authDTO.setUsername(authRequest.getUsername());
            authDTO.setRole(userOptional.get().getRole().getName());
            return authDTO;

        } else {
            throw new UserNotFoundException();
        }
    }

    @Override
    public AuthDTO register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistException();
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UsernameAlreadyExistException("Email already exists");
        }

        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_USER");
            return roleRepository.save(newRole);
        });

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(encodedPassword);
        newUser.setRole(role);
        newUser.setEmail(registerRequest.getEmail());

        UserStatus activeStatus = userStatusRepository.findByStatus("ACTIVE")
                .orElseThrow(() -> new RuntimeException("ACTIVE status not found"));
        newUser.setStatus(activeStatus);

        userRepository.save(newUser);

        // Publish user registered event using in-house publisher
        UserRegistrationWriteEvent event = new UserRegistrationWriteEvent(this, newUser);
        userRegistrationWritePublisher.publish(event);

        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername(registerRequest.getUsername());
        return authDTO;
    }

    @Override
    public TokenDTO refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtAuthenticationFilter.extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new TokenException("Missing refresh token");
        }

        try {
            String newAccessToken = tokenValidation(response, refreshToken);

            TokenDTO tokenDTO = new TokenDTO();
            tokenDTO.setAccessToken(newAccessToken);
            return tokenDTO;
        } catch (JwtException ex) {
            throw new JwtException(INVALID_REFRESH_TOKEN);
        }
    }

    @Override
    public CheckSessionDTO checkSession(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtAuthenticationFilter.extractAccessTokenFromCookie(request);
        String refreshToken = jwtAuthenticationFilter.extractRefreshTokenFromCookie(request);

        if (accessToken == null && refreshToken != null) {
            accessToken = tokenValidation(response, refreshToken);
        }

        CheckSessionDTO checkSessionDTO = new CheckSessionDTO();
        checkSessionDTO.setLoggedIn(false);

        if (accessToken != null) {
            try {
                String username = jwtService.extractUsername(accessToken, false);
                if (username != null && jwtService.isTokenValid(accessToken, username, false)) {
                    Optional<User> userOptional = userRepository.findByUsername(username); // Fetch user
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        checkSessionDTO.setLoggedIn(true);
                        checkSessionDTO.setUsername(username);
                        checkSessionDTO.setRole(user.getRole().getName()); // <-- ADD THIS LINE
                        checkSessionDTO.setAccessToken(accessToken);
                        return checkSessionDTO;
                    }
                }
            } catch (JwtException ex) {
                // Token is invalid or expired
            }
        }
        return checkSessionDTO;
    }


    @Override
    public String logout(RefreshRequest refreshRequest, HttpServletResponse response) {
        String refreshTokenStr = refreshRequest.getRefreshToken();
        refreshTokenService.getValidRefreshToken(refreshTokenStr).ifPresent(refreshTokenService::revokeToken);

        Cookie accessTokenCookie = new Cookie(CommonConstants.ACCESS_TOKEN, "");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);

        Cookie refreshTokenCookie = new Cookie(CommonConstants.REFRESH_TOKEN, "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return "Logged out successfully";
    }

    private String tokenValidation(HttpServletResponse response, String refreshToken) {
        String username = jwtService.extractUsername(refreshToken, true);
        if (!jwtService.isTokenValid(refreshToken, username, true)) {
            throw new TokenException(INVALID_REFRESH_TOKEN);
        }

        Optional<RefreshToken> validToken = refreshTokenService.getValidRefreshToken(refreshToken);
        if (validToken.isEmpty()) {
            throw new TokenException(INVALID_REFRESH_TOKEN);
        }

        User user = validToken.get().getUser();
        String newAccessToken = jwtService.generateAccessToken(user.getUsername(), Set.of(user.getRole()));
        Cookie accessTokenCookie = new Cookie(CommonConstants.ACCESS_TOKEN, newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(accessTokenCookieExpiry);
        response.addCookie(accessTokenCookie);

        return newAccessToken;
    }

    @Override
    public boolean resetPassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new SameNewPasswordException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public Long getUserId(String username) {
        return userRepository.getUserIdByUsername(username);
    }
}
