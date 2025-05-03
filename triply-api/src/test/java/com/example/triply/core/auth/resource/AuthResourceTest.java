package com.example.triply.core.auth.resource;

import com.example.triply.core.auth.dto.*;
import com.example.triply.core.auth.service.AuthService;
import com.example.triply.core.auth.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthResourceTest {

    private MockMvc mockMvc;
    private AuthService authService;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        jwtService = mock(JwtService.class);
        AuthResource authResource = new AuthResource(authService, jwtService);
        mockMvc = MockMvcBuilders.standaloneSetup(authResource)
                .addPlaceholderValue("triply.api-version", "v1")
                .build();
    }

    @Test
    void loginTest_shouldReturnDone() throws Exception {
        mockMvc.perform(get("/api/v1/auth/loginTest"))
                .andExpect(status().isOk())
                .andExpect(content().string("Done"));
    }

    @Test
    void login_shouldReturnSuccessResponse() throws Exception {
        AuthDTO mockDto = new AuthDTO();
        mockDto.setMessage("Logged in");

        when(authService.login(any(AuthRequest.class), any(HttpServletResponse.class)))
                .thenReturn(mockDto);

        String json = """
                {
                    "username": "testuser",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged in"));
    }

    @Test
    void login_shouldReturnUnauthorizedOnException() throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("user");
        authRequest.setPassword("pass");

        when(authService.login(any(AuthRequest.class), any(HttpServletResponse.class)))
                .thenThrow(new RuntimeException("Login failed"));

        String json = """
        {
          "username": "user",
          "password": "pass"
        }
        """;

        // When / Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("An error occurred during login: Login failed"));
    }

    @Test
    void resetPassword_shouldReturnUpdatedMessage() throws Exception {
        when(jwtService.extractUsernameFromRequest(any(HttpServletRequest.class))).thenReturn("user");
        when(authService.resetPassword(eq("user"), eq("oldpass"), eq("newpass"))).thenReturn(true);

        String json = """
                {
                    "currentPassword": "oldpass",
                    "newPassword": "newpass"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully"));
    }

    @Test
    void testResetPassword_shouldReturnUnauthorizedWhenUsernameNull() throws Exception {
        when(jwtService.extractUsernameFromRequest(any(HttpServletRequest.class))).thenReturn(null);

        String json = """
        {
          "currentPassword": "oldpass",
          "newPassword": "newpass"
        }
        """;

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Not authenticated"));
    }

    @Test
    void testResetPassword_shouldReturnBadRequestWhenFieldsBlank() throws Exception {
        when(jwtService.extractUsernameFromRequest(any(HttpServletRequest.class))).thenReturn("user");

        String json = """
        {
          "currentPassword": "",
          "newPassword": ""
        }
        """;

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password fields cannot be blank"));
    }

    @Test
    void testResetPassword_shouldReturnBadRequestWhenPasswordsSame() throws Exception {
        when(jwtService.extractUsernameFromRequest(any(HttpServletRequest.class))).thenReturn("user");

        String json = """
        {
          "currentPassword": "samepass",
          "newPassword": "samepass"
        }
        """;

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("New password cannot be the same as current password"));
    }

    @Test
    void testResetPassword_shouldReturnBadRequestWhenResetFails() throws Exception {
        when(jwtService.extractUsernameFromRequest(any(HttpServletRequest.class))).thenReturn("user");
        when(authService.resetPassword(eq("user"), eq("oldpass"), eq("newpass"))).thenReturn(false);

        String json = """
        {
          "currentPassword": "oldpass",
          "newPassword": "newpass"
        }
        """;

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password could not be updated"));
    }

    @Test
    void testResetPassword_shouldReturnBadRequestOnException() throws Exception {
        when(jwtService.extractUsernameFromRequest(any(HttpServletRequest.class))).thenReturn("user");
        when(authService.resetPassword(eq("user"), eq("oldpass"), eq("newpass")))
                .thenThrow(new RuntimeException("Service error"));

        String json = """
        {
          "currentPassword": "oldpass",
          "newPassword": "newpass"
        }
        """;

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Service error"));
    }

    @Test
    void getUserId_shouldReturnUserId() throws Exception {
        when(jwtService.extractUsernameFromRequest(any(HttpServletRequest.class))).thenReturn("user");
        when(authService.getUserId("user")).thenReturn(42L);

        mockMvc.perform(get("/api/v1/auth/userid"))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }

    @Test
    void testGetUserId_shouldReturnUnauthorizedWhenUsernameIsNull() throws Exception {
        // Given: jwtService returns null
        when(jwtService.extractUsernameFromRequest(any(HttpServletRequest.class))).thenReturn(null);

        // When / Then
        mockMvc.perform(get("/api/v1/auth/userid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegister_shouldReturnInternalServerErrorOnException() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("DB down"));

        String json = """
        {
          "username": "testuser",
          "password": "password",
          "email": "test@example.com"
        }
        """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An error occurred during registration: DB down"));
    }

    @Test
    void testRefresh_shouldReturnForbiddenOnException() throws Exception {
        when(authService.refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("An error occurred during refresh token: Invalid token"));
    }

    @Test
    void testLogout_shouldReturnSuccessMessage() throws Exception {
        when(authService.logout(any(RefreshRequest.class), any(HttpServletResponse.class)))
                .thenReturn("Logout successful");

        String json = """
        {
          "refreshToken": "abc123"
        }
        """;

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"));
    }

    @Test
    void testCheckSession_shouldReturnForbiddenOnException() throws Exception {
        when(authService.checkSession(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenThrow(new RuntimeException("Session invalid"));

        mockMvc.perform(get("/api/v1/auth/check-session"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mesasge").value("An error occurred during check session: Session invalid"));
    }

}
