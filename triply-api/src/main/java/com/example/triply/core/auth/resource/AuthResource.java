package com.example.triply.core.auth.resource;

import com.example.triply.core.auth.dto.*;
import com.example.triply.core.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${triply.api-version}/auth")
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/loginTest")
    public ResponseEntity<String> login() {
        return ResponseEntity.status(HttpStatus.OK).body("Done");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
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

}