package com.example.triply.core.auth.service.impl;

import com.example.triply.common.constants.CommonConstants;
import com.example.triply.common.exception.TokenException;
import com.example.triply.common.exception.UserNotFoundException;
import com.example.triply.common.exception.UsernameAlreadyExistException;
import com.example.triply.common.filter.JwtAuthenticationFilter;
import com.example.triply.core.admin.entity.UserStatus;
import com.example.triply.core.admin.repository.UserStatusRepository;
import com.example.triply.core.auth.dto.*;
import com.example.triply.core.auth.entity.RefreshToken;
import com.example.triply.core.auth.entity.Role;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.RoleRepository;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.auth.service.AuthService;
import com.example.triply.core.auth.service.JwtService;
import com.example.triply.core.auth.service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserStatusRepository userStatusRepository, @Lazy AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenService refreshTokenService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userStatusRepository = userStatusRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Override
    public AuthDTO login(AuthRequest authRequest, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        Optional<User> userOptional = userRepository.findByUsername(authRequest.getUsername());
        if (userOptional.isPresent()) {
            String accessToken = jwtService.generateAccessToken(authRequest.getUsername(), userOptional.get().getRoles());
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
        newUser.setEmail(registerRequest.getEmail());
        newUser.setRoles(Set.of(role));

        UserStatus activeStatus = userStatusRepository.findByStatus("ACTIVE")
                .orElseThrow(() -> new RuntimeException("ACTIVE status not found"));
        newUser.setStatus(activeStatus);

        userRepository.save(newUser);

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
            throw new JwtException("Invalid refresh token");
        }
    }

    @Override
    public CheckSessionDTO checkSession(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtAuthenticationFilter.extractAccessTokenFromCookie(request);
        String refreshToken = jwtAuthenticationFilter.extractRefreshTokenFromCookie(request);

        CheckSessionDTO checkSessionDTO = new CheckSessionDTO();
        checkSessionDTO.setLoggedIn(false);

        if (accessToken != null) {
            try {
                String username = jwtService.extractUsername(accessToken, false);
                if (username != null && jwtService.isTokenValid(accessToken, username, false)) {

                    checkSessionDTO.setLoggedIn(true);
                    checkSessionDTO.setUsername(username);
                    checkSessionDTO.setAccessToken(accessToken);
                    return checkSessionDTO;
                }
            } catch (ExpiredJwtException ex) {
                throw new TokenException("Access token expired");
            } catch (JwtException ex) {
                throw new TokenException("Invalid access token");
            }
        }

        if (refreshToken != null) {
            String username = jwtService.extractUsername(refreshToken, false);
            String newAccessToken = tokenValidation(response, refreshToken);

            checkSessionDTO.setLoggedIn(true);
            checkSessionDTO.setUsername(username);
            checkSessionDTO.setAccessToken(newAccessToken);

            return checkSessionDTO;
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
            throw new TokenException("Invalid refresh token");
        }

        Optional<RefreshToken> validToken = refreshTokenService.getValidRefreshToken(refreshToken);
        if (validToken.isEmpty()) {
            throw new TokenException("Invalid refresh token");
        }

        User user = validToken.get().getUser();
        String newAccessToken = jwtService.generateAccessToken(user.getUsername(), user.getRoles());

        Cookie accessTokenCookie = new Cookie(CommonConstants.ACCESS_TOKEN, newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(accessTokenCookieExpiry);
        response.addCookie(accessTokenCookie);

        return newAccessToken;
    }
}
