package com.example.triply.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.csrf.CsrfToken;

import java.io.IOException;

import static org.mockito.Mockito.*;

class CsrfTokenResponseFilterTest {

    private CsrfTokenResponseFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new CsrfTokenResponseFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    void testCsrfTokenPresent_setsHeader() throws ServletException, IOException {
        CsrfToken csrfToken = mock(CsrfToken.class);
        when(csrfToken.getToken()).thenReturn("test-csrf-token");
        when(request.getAttribute("_csrf")).thenReturn(csrfToken);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader("X-XSRF-TOKEN", "test-csrf-token");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testCsrfTokenMissing_doesNotSetHeader() throws ServletException, IOException {
        when(request.getAttribute("_csrf")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response, never()).setHeader(eq("X-XSRF-TOKEN"), anyString());
        verify(filterChain).doFilter(request, response);
    }
}
