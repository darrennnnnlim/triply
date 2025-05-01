package com.example.triply.core.auth.resource;

import com.example.triply.core.auth.dto.*;
import com.example.triply.core.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.triply.core.auth.service.JwtService;
import org.springframework.util.StringUtils;

import java.util.Map;

@RestController
@RequestMapping("/api/${triply.api-version}/auth")
public class AuthResource {

    private final AuthService authService;
    private final JwtService jwtService;
    private static final String MESSAGE = "message";
    private static final String NOT_AUTHENTICATED = "Not authenticated";
    private static final String NEW_PASSWORD_SAME = "New password cannot be the same as current password";
    private static final String PASSWORD_UPDATED = "Password updated successfully";
    private static final String PASSWORD_NOT_UPDATED = "Password could not be updated";

    @Autowired
    public AuthResource(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @GetMapping("/loginTest")
    public ResponseEntity<String> login() {
        return ResponseEntity.status(HttpStatus.OK).body("Done");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        System.out.println("Received login request for username: " + authRequest.getUsername());
        try {
            AuthDTO login = authService.login(authRequest, response);
            return ResponseEntity.status(HttpStatus.OK).body(login);
        } catch (Exception e) {
            AuthDTO errorDTO = new AuthDTO();
            errorDTO.setMessage("An error occurred during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDTO);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDTO> register(@RequestBody RegisterRequest registerRequest) {
        try {
            AuthDTO register = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(register);
        } catch (Exception e) {
            AuthDTO errorDTO = new AuthDTO();
            errorDTO.setMessage("An error occurred during registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenDTO> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            TokenDTO token = authService.refreshToken(request, response);
            return ResponseEntity.status(HttpStatus.OK).body(token);
        } catch (Exception e) {
            TokenDTO errorDTO = new TokenDTO();
            errorDTO.setMessage("An error occurred during refresh token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshRequest refreshRequest, HttpServletResponse response) {
        String logout = authService.logout(refreshRequest, response);
        return ResponseEntity.status(HttpStatus.OK).body(logout);
    }

    @GetMapping("/check-session")
    public ResponseEntity<CheckSessionDTO> checkSession(HttpServletRequest request, HttpServletResponse response) {
        try {
            CheckSessionDTO checkSessionDTO = authService.checkSession(request, response);
            return ResponseEntity.status(HttpStatus.OK).body(checkSessionDTO);
        } catch (Exception e) {
            CheckSessionDTO errorDTO = new CheckSessionDTO();
            errorDTO.setMesasge("An error occurred during check session: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPassword request, HttpServletRequest httpRequest) {
        String username = jwtService.extractUsernameFromRequest(httpRequest);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(MESSAGE, NOT_AUTHENTICATED));
        }
        if (!StringUtils.hasText(request.getCurrentPassword()) || !StringUtils.hasText(request.getNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(MESSAGE, "Password fields cannot be blank"));
        }
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(MESSAGE, NEW_PASSWORD_SAME));
        }
        try {
            boolean result = authService.resetPassword(username, request.getCurrentPassword(), request.getNewPassword());
            if (result) {
                return ResponseEntity.ok().body(Map.of(MESSAGE, PASSWORD_UPDATED));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(MESSAGE, PASSWORD_NOT_UPDATED));
            }
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(MESSAGE, ex.getMessage()));
        }
    }
}