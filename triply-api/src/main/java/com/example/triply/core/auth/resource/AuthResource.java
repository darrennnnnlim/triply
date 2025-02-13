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
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthResource(JwtService jwtService, @Lazy AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/loginTest")
    public ResponseEntity<String> login() {
        return ResponseEntity.status(HttpStatus.OK).body("Done");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            Optional<User> userOptional = userRepository.findByUsername(authRequest.getUsername());
            if (userOptional.isPresent()) {
                String accessToken = jwtService.generateAccessToken(authRequest.getUsername(), authRequest.getRole());
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(userOptional.get());

                Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
                accessTokenCookie.setHttpOnly(true);
                accessTokenCookie.setSecure(true);
                accessTokenCookie.setPath("/");
                accessTokenCookie.setMaxAge(accessTokenCookieExpiry);

                Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken.getToken());
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
        newUser.setRole(role);
        userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Missing refresh token");
        }

        String username = jwtService.extractUsername(refreshToken, true);
        if (jwtService.isTokenValid(refreshToken, username, true)) {
            Optional<RefreshToken> refreshTokenOpt = refreshTokenService.getValidRefreshToken(refreshToken);
            if (refreshTokenOpt.isPresent()) {
                String newAccessToken = jwtService.generateAccessToken(username, "USER");

                Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
                accessTokenCookie.setHttpOnly(true);
                accessTokenCookie.setSecure(true);
                accessTokenCookie.setPath("/");
                accessTokenCookie.setMaxAge(accessTokenCookieExpiry);

                response.addCookie(accessTokenCookie);

                return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired refresh token");
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshRequest refreshRequest) {
        String refreshTokenStr = refreshRequest.getRefreshToken();

        refreshTokenService.getValidRefreshToken(refreshTokenStr).ifPresent(refreshTokenService::revokeToken);

        return ResponseEntity.ok("Logged out successfully");
    }
}
