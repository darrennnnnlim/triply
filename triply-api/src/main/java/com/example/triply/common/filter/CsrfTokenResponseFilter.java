package com.example.triply.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CsrfTokenResponseFilter extends OncePerRequestFilter {

    private final CsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");

        if (csrfToken != null) {
            response.setHeader("X-XSRF-TOKEN", csrfToken.getToken());
        }

        filterChain.doFilter(request, response);
    }
}
