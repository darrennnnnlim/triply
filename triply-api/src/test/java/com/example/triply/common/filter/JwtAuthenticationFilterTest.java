package com.example.triply.common.filter;

import com.example.triply.common.constants.CommonConstants;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock private JwtService jwtService;
    @Mock private UserRepository userRepository;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        //Disable logger for unit tests
        org.slf4j.Logger root = org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if (root instanceof ch.qos.logback.classic.Logger logger) {
            logger.setLevel(ch.qos.logback.classic.Level.OFF);
        }

        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter(jwtService, userRepository);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws Exception {
        String token = "valid.jwt.token";
        String username = "testUser";
        List<String> roles = List.of("ROLE_USER");

        User user = new User();
        user.setUsername(username);

        Cookie accessTokenCookie = new Cookie(CommonConstants.ACCESS_TOKEN, token);
        when(request.getCookies()).thenReturn(new Cookie[]{accessTokenCookie});
        when(jwtService.extractUsername(token, false)).thenReturn(username);
        when(jwtService.extractRoles(token)).thenReturn(roles);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(token, username, false)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidToken_doesNotSetAuthentication() throws Exception {
        String token = "invalid.jwt.token";
        Cookie accessTokenCookie = new Cookie(CommonConstants.ACCESS_TOKEN, token);
        when(request.getCookies()).thenReturn(new Cookie[]{accessTokenCookie});
        when(jwtService.extractUsername(token, false)).thenThrow(new RuntimeException("Invalid"));

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_noToken_doesNothing() throws Exception {
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void extractAccessTokenFromCookie_returnsToken() {
        Cookie cookie = new Cookie(CommonConstants.ACCESS_TOKEN, "abc.def.ghi");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String token = filter.extractAccessTokenFromCookie(request);

        assertEquals("abc.def.ghi", token);
    }

    @Test
    void extractRefreshTokenFromCookie_returnsToken() {
        Cookie cookie = new Cookie(CommonConstants.REFRESH_TOKEN, "refresh.token.here");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String token = filter.extractRefreshTokenFromCookie(request);

        assertEquals("refresh.token.here", token);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
