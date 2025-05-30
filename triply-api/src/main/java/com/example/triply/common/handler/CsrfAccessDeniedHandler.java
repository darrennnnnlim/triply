package com.example.triply.common.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CsrfAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsrfAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if ("Could not verify the provided CSRF token because no token was found to compare.".equals(accessDeniedException.getMessage())) {
            response.setStatus(419);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"CSRF token invalid or missing\"}");
        } else {
            LOGGER.error(accessDeniedException.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // fallback
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Access denied\"}");
        }
    }
}
