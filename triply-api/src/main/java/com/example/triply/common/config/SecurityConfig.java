package com.example.triply.common.config;

import com.example.triply.common.filter.JwtAuthenticationFilter;
import com.example.triply.common.handler.CsrfAccessDeniedHandler;
import com.example.triply.core.auth.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SecurityConfig {

    @Value("${triply.cors.allowedOrigins}")
    private String allowedOrigins;

    @Value("${triply.cors.allowedMethods}")
    private String allowedMethods;

    @Value("${triply.cors.allowedHeaders}")
    private String allowedHeaders;

    @Value("${triply.cors.exposedHeaders}")
    private String exposedHeaders;

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CsrfAccessDeniedHandler csrfAccessDeniedHandler;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter, CsrfAccessDeniedHandler csrfAccessDeniedHandler) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.csrfAccessDeniedHandler = csrfAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        System.out.println("Configuring security filter chain...");
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/{version}/auth/**", "/api/{version}/booking/**", "/api/v1/ratings/**", "/api/v1/reset-password", "/test/**", "/api/v1/priceThreshold")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(customCsrfTokenRequestHandler()))
                .exceptionHandling(exception -> exception.accessDeniedHandler(csrfAccessDeniedHandler))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/{version}/auth/login/**", "/api/{version}/auth/register/**", "/api/{version}/auth/reset-password/**").permitAll()
                        .requestMatchers("/api/{version}/auth/reset-password", "/api/{version}/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/api/v1/ratings/**", "/api/v1/booking/**", "/api/v1/flight/**", "/api/v1/hotel/**", "/api/v1/flightsearch", "/api/v1/priceThreshold").permitAll()
                        .requestMatchers("/api/{version}/auth/check-session").authenticated()
                        .requestMatchers("/api/{version}/auth/refresh").permitAll()
                        .requestMatchers("/api/{version}/admin/user/**").authenticated()
                        .requestMatchers("/api/{version}/admin/currentuser").authenticated()
                        .requestMatchers("/api/{version}/booking/test").hasRole("USER")
                        .requestMatchers("/api/{version}/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/{version}/booking/test", "/api/{version}/ratings/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CsrfTokenRequestHandler customCsrfTokenRequestHandler() {
        return new CsrfTokenRequestHandler() {

            private final CsrfTokenRequestAttributeHandler delegate = new CsrfTokenRequestAttributeHandler();

            public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
                delegate.handle(request, response, csrfToken);

                if (csrfToken != null && !hasXsrfCookieAlreadySet(response)) {
                    ResponseCookie cookie = ResponseCookie.from("XSRF-TOKEN", csrfToken.get().getToken())
                            .path("/")
                            .httpOnly(false)
                            .secure(true)
                            .sameSite("None")
                            .domain("darrennnnnlim.com")
                            .build();

                    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                }
            }
        };
    }

    private boolean hasXsrfCookieAlreadySet(HttpServletResponse response) {
        Collection<String> setCookies = response.getHeaders(HttpHeaders.SET_COOKIE);
        return setCookies.stream().anyMatch(header -> header.startsWith("XSRF-TOKEN="));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        corsConfig.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        corsConfig.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        corsConfig.setExposedHeaders(Arrays.asList(exposedHeaders.split(",")));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
