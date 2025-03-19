package com.example.triply.core.auth.resource;

import com.example.triply.common.constants.CommonConstants;
import com.example.triply.common.filter.JwtAuthenticationFilter;
import com.example.triply.core.auth.dto.AuthRequest;
import com.example.triply.core.auth.dto.RefreshRequest;
import com.example.triply.core.auth.dto.RegisterRequest;
import com.example.triply.core.auth.entity.RefreshToken;
import com.example.triply.core.auth.entity.Role;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.admin.entity.UserStatus;
import com.example.triply.core.auth.repository.RoleRepository;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.admin.repository.UserStatusRepository;
import com.example.triply.core.auth.service.JwtService;
import com.example.triply.core.auth.service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/${triply.api-version}/auth")
public class AuthResource {

    @Value("${jwt.access-token.cookie-expiry}")
    private int accessTokenCookieExpiry;

    @Value("${jwt.refresh-token.cookie-expiry}")
    private int refreshTokenCookieExpiry;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserStatusRepository userStatusRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public AuthResource(JwtService jwtService, @Lazy AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, UserStatusRepository userStatusRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userStatusRepository= userStatusRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @GetMapping("/loginTest")
    public ResponseEntity<String> login() {
        return ResponseEntity.status(HttpStatus.OK).body("Done");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
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

                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("message", "Login Successful"));

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already taken.");
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
            newUser.setRoles(Set.of(role));

            UserStatus activeStatus = userStatusRepository.findByStatus("ACTIVE")
                    .orElseThrow(() -> new RuntimeException("ACTIVE status not found"));
            newUser.setStatus(activeStatus);

            userRepository.save(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during registration: " + e.getMessage());
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtAuthenticationFilter.extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
        }

        try {
            String username = jwtService.extractUsername(refreshToken, true);
            if (!jwtService.isTokenValid(refreshToken, username, true)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
            }

            Optional<RefreshToken> validToken = refreshTokenService.getValidRefreshToken(refreshToken);
            if (!validToken.isPresent()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token revoked");
            }

            User user = validToken.get().getUser();
            String newAccessToken = jwtService.generateAccessToken(user.getUsername(), user.getRoles());

            Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(accessTokenCookieExpiry);
            response.addCookie(accessTokenCookie);

            return ResponseEntity.ok().body(Map.of("accessToken", newAccessToken));
        } catch (JwtException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshRequest refreshRequest, HttpServletResponse response) {
        String refreshTokenStr = refreshRequest.getRefreshToken();
        refreshTokenService.getValidRefreshToken(refreshTokenStr).ifPresent(refreshTokenService::revokeToken);

        Cookie accessTokenCookie = new Cookie("accessToken", "");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);

        Cookie refreshTokenCookie = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtAuthenticationFilter.extractAccessTokenFromCookie(request);
        String refreshToken = jwtAuthenticationFilter.extractRefreshTokenFromCookie(request);

        if (accessToken != null) {
            try {
                String username = jwtService.extractUsername(accessToken, false);
                if (username != null && jwtService.isTokenValid(accessToken, username, false)) {
                    return ResponseEntity.ok().body(Map.of("isLoggedIn", true, "username", username));
                }
            } catch (ExpiredJwtException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("code", "TOKEN_EXPIRED", "message", "Access token expired"));
            } catch (JwtException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("code", "INVALID_TOKEN", "message", "Invalid access token"));
            }
        }

        if (refreshToken != null) {
            String username = jwtService.extractUsername(refreshToken, true);
            if (!jwtService.isTokenValid(refreshToken, username, true)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
            }

            Optional<RefreshToken> validToken = refreshTokenService.getValidRefreshToken(refreshToken);
            if (!validToken.isPresent()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token revoked");
            }

            User user = validToken.get().getUser();
            String newAccessToken = jwtService.generateAccessToken(user.getUsername(), user.getRoles());

            Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(accessTokenCookieExpiry);
            response.addCookie(accessTokenCookie);

            return ResponseEntity.ok().body(Map.of("isLoggedIn", true, "username", username));
        }

        return ResponseEntity.ok().body(Map.of("isLoggedIn", false));
    }

}