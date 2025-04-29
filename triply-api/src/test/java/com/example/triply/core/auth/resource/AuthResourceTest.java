//package com.example.triply.core.auth.resource;
//
//import com.example.triply.core.auth.dto.AuthRequest;
//import com.example.triply.core.auth.dto.AuthResponse;
//import com.example.triply.core.auth.dto.RefreshRequest;
//import com.example.triply.core.auth.dto.RegisterRequest;
//import com.example.triply.core.auth.entity.RefreshToken;
//import com.example.triply.core.auth.entity.Role;
//import com.example.triply.core.auth.entity.User;
//import com.example.triply.core.auth.repository.RoleRepository;
//import com.example.triply.core.auth.repository.UserRepository;
//import com.example.triply.core.auth.service.JwtService;
//import com.example.triply.core.auth.service.RefreshTokenService;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class AuthResourceTest {
//
//    @InjectMocks
//    private AuthResource authResource;
//
//    @Mock
//    private AuthService authService;
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(authResource).build();
//    }
//
//    @Test
//    void testLoginTest() throws Exception {
//        mockMvc.perform(get("/api/v1/auth/loginTest"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Done"));
//    }
//
//    @Test
//    void testLogin_Success() throws Exception {
//        AuthRequest request = new AuthRequest();
//        AuthDTO responseDTO = new AuthDTO();
//        responseDTO.setToken("mockToken");
//
//        when(authService.login(any(AuthRequest.class), any(HttpServletResponse.class)))
//                .thenReturn(responseDTO);
//
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"username\":\"user\",\"password\":\"pass\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("mockToken"));
//    }
//
//    @Test
//    void testLogin_Exception() throws Exception {
//        when(authService.login(any(AuthRequest.class), any(HttpServletResponse.class)))
//                .thenThrow(new RuntimeException("fail"));
//
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"username\":\"user\",\"password\":\"pass\"}"))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.message", containsString("fail")));
//    }
//
//    @Test
//    void testRegister_Success() throws Exception {
//        RegisterRequest request = new RegisterRequest();
//        AuthDTO responseDTO = new AuthDTO();
//        responseDTO.setToken("registered");
//
//        when(authService.register(any(RegisterRequest.class)))
//                .thenReturn(responseDTO);
//
//        mockMvc.perform(post("/api/v1/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"username\":\"user\",\"password\":\"pass\"}"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.token").value("registered"));
//    }
//
//    @Test
//    void testRegister_Exception() throws Exception {
//        when(authService.register(any(RegisterRequest.class)))
//                .thenThrow(new RuntimeException("error"));
//
//        mockMvc.perform(post("/api/v1/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"username\":\"user\",\"password\":\"pass\"}"))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.message", containsString("error")));
//    }
//
//    @Test
//    void testRefreshToken_Success() throws Exception {
//        TokenDTO tokenDTO = new TokenDTO();
//        tokenDTO.setAccessToken("access");
//
//        when(authService.refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class)))
//                .thenReturn(tokenDTO);
//
//        mockMvc.perform(post("/api/v1/auth/refresh"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.accessToken").value("access"));
//    }
//
//    @Test
//    void testRefreshToken_Exception() throws Exception {
//        when(authService.refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class)))
//                .thenThrow(new RuntimeException("fail"));
//
//        mockMvc.perform(post("/api/v1/auth/refresh"))
//                .andExpect(status().isForbidden())
//                .andExpect(jsonPath("$.message", containsString("fail")));
//    }
//
//    @Test
//    void testLogout() throws Exception {
//        when(authService.logout(any(RefreshRequest.class), any(HttpServletResponse.class)))
//                .thenReturn("Logged out");
//
//        mockMvc.perform(post("/api/v1/auth/logout")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"refreshToken\":\"token\"}"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Logged out"));
//    }
//
//    @Test
//    void testCheckSession_Success() throws Exception {
//        CheckSessionDTO dto = new CheckSessionDTO();
//        dto.setValid(true);
//
//        when(authService.checkSession(any(HttpServletRequest.class), any(HttpServletResponse.class)))
//                .thenReturn(dto);
//
//        mockMvc.perform(get("/api/v1/auth/check-session"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.valid").value(true));
//    }
//
//    @Test
//    void testCheckSession_Exception() throws Exception {
//        when(authService.checkSession(any(HttpServletRequest.class), any(HttpServletResponse.class)))
//                .thenThrow(new RuntimeException("fail"));
//
//        mockMvc.perform(get("/api/v1/auth/check-session"))
//                .andExpect(status().isForbidden())
//                .andExpect(jsonPath("$.mesasge", containsString("fail")));
//    }
//}