package com.example.triply.common.audit;

import com.example.triply.core.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpringSecurityAuditorAwareTest {

    private SpringSecurityAuditorAware auditorAware;

    @BeforeEach
    void setUp() {
        auditorAware = new SpringSecurityAuditorAware();
    }

    @Test
    void testGetCurrentAuditor_nullAuthentication_returnsSystem() {
        try (MockedStatic<SecurityContextHolder> contextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            contextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            Optional<String> auditor = auditorAware.getCurrentAuditor();
            assertEquals(Optional.of("System"), auditor);
        }
    }

    @Test
    void testGetCurrentAuditor_notAuthenticated_returnsSystem() {
        try (MockedStatic<SecurityContextHolder> contextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            contextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(false);

            Optional<String> auditor = auditorAware.getCurrentAuditor();
            assertEquals(Optional.of("System"), auditor);
        }
    }

    @Test
    void testGetCurrentAuditor_anonymousToken_returnsSystem() {
        try (MockedStatic<SecurityContextHolder> contextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            AnonymousAuthenticationToken token = mock(AnonymousAuthenticationToken.class);

            contextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(token);
            when(token.isAuthenticated()).thenReturn(true);

            Optional<String> auditor = auditorAware.getCurrentAuditor();
            assertEquals(Optional.of("System"), auditor);
        }
    }

    @Test
    void testGetCurrentAuditor_userPrincipal_returnsUsername() {
        try (MockedStatic<SecurityContextHolder> contextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            User user = new User();
            user.setUsername("darren");

            contextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(user);

            Optional<String> auditor = auditorAware.getCurrentAuditor();
            assertEquals(Optional.of("darren"), auditor);
        }
    }

    @Test
    void testGetCurrentAuditor_stringPrincipal_returnsPrincipalString() {
        try (MockedStatic<SecurityContextHolder> contextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            contextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn("admin"); // Plain String

            Optional<String> auditor = auditorAware.getCurrentAuditor();
            assertEquals(Optional.of("admin"), auditor);
        }
    }

}
