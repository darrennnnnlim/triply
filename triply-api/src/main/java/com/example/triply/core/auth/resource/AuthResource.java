package com.example.triply.core.auth.resource;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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