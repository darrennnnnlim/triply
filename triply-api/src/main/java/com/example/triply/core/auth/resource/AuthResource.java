package com.example.triply.core.auth.resource;

import com.example.triply.core.auth.dto.AuthRequest;
import com.example.triply.core.auth.dto.AuthResponse;
import com.example.triply.core.auth.dto.RefreshRequest;
import com.example.triply.core.auth.dto.RegisterRequest;
import com.example.triply.core.auth.entity.Role;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.RoleRepository;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.auth.service.JwtService;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${triply.api-version}/auth")
public class AuthResource {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResource(JwtService jwtService, @Lazy AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/loginTest")
    public ResponseEntity<String> login() {
        return ResponseEntity.status(HttpStatus.OK).body("Done");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            String accessToken = jwtService.generateAccessToken(authRequest.getUsername(), authRequest.getRole());
            String refreshToken = jwtService.generateRefreshToken(authRequest.getUsername());
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
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
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshRequest) {
        String username = jwtService.extractUsername(refreshRequest.getRefreshToken(), true);
        if (jwtService.isTokenValid(refreshRequest.getRefreshToken(), username, true)) {
            String newAccessToken = jwtService.generateAccessToken(username, refreshRequest.getRole());
            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshRequest.getRefreshToken()));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
    }
}
