package com.example.triply.common.config;

import com.example.triply.common.filter.CsrfTokenResponseFilter;
import com.example.triply.common.filter.JwtAuthenticationFilter;
import com.example.triply.core.auth.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
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

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        System.out.println("Configuring security filter chain...");
        CsrfTokenRequestAttributeHandler requestAttributeHandler = new CsrfTokenRequestAttributeHandler();
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/{version}/auth/**", "/api/{version}/booking/**", "/api/v1/ratings/**", "/test/**") // Added /test/**
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(requestAttributeHandler))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/{version}/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/api/v1/ratings/**", "/api/v1/booking/**", "/api/v1/flight/**", "/api/v1/hotel/**").permitAll()
                        .requestMatchers("/api/{version}/auth/check-session").permitAll()
                        .requestMatchers("/api/{version}/auth/refresh").permitAll()
                        .requestMatchers("/api/{version}/admin/currentuser").permitAll()
                        .requestMatchers("/api/{version}/booking/test").hasRole("USER")
                        .requestMatchers("/api/{version}/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/{version}/booking/test", "/api/{version}/ratings/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new CsrfTokenResponseFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
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
