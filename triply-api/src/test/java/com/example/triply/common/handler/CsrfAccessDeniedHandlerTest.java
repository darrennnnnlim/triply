package com.example.triply.common.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

class CsrfAccessDeniedHandlerTest {

    private CsrfAccessDeniedHandler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        handler = new CsrfAccessDeniedHandler();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void handle_withCsrfTokenMissing_sets419AndCustomMessage() throws IOException, ServletException {
        AccessDeniedException ex = new AccessDeniedException("Could not verify the provided CSRF token because no token was found to compare.");

        handler.handle(request, response, ex);

        verify(response).setStatus(419);
        verify(response).setContentType("application/json");
        verify(writer).write("{\"error\": \"CSRF token invalid or missing\"}");
    }

    @Test
    void handle_withOtherAccessDenied_sets403AndGenericMessage() throws IOException, ServletException {
        AccessDeniedException ex = new AccessDeniedException("Some other access issue");

        handler.handle(request, response, ex);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json");
        verify(writer).write("{\"error\": \"Access denied\"}");
    }
}
