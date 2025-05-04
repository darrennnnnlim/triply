package com.example.triply.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private CsrfTokenRequestHandler csrfTokenRequestHandler;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void testPasswordEncoder() {
        String encoded = passwordEncoder.encode("abc123");
        assertThat(passwordEncoder.matches("abc123", encoded)).isTrue();
    }

    @Test
    void testCorsConfig() {
        assertThat(corsConfigurationSource).isNotNull();
    }

    @Test
    void testCsrfHandler() {
        var req = new MockHttpServletRequest();
        var res = new MockHttpServletResponse();

        CsrfToken token = new CsrfToken() {
            public String getHeaderName() { return "X-XSRF-TOKEN"; }
            public String getParameterName() { return "_csrf"; }
            public String getToken() { return "mock-token"; }
        };

        csrfTokenRequestHandler.handle(req, res, () -> token);
        assertThat(res.getHeader("Set-Cookie")).contains("XSRF-TOKEN=mock-token");
    }

    @Test
    void testSecurityFilterChainLoads() {
        assertThat(securityFilterChain).isNotNull();
    }
}

