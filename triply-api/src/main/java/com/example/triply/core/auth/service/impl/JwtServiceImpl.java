package com.example.triply.core.auth.service.impl;

import com.example.triply.core.auth.entity.Role;
import com.example.triply.core.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-secret-key}")
    private String refreshSecretKey;

    @Value("${jwt.access-token.expiry}")
    private int accessTokenExpiry;

    @Value("${jwt.refresh-token.expiry}")
    private Long refreshTokenExpiry;

    public String generateAccessToken(String username, Set<Role> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiry);

        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiry)
                .claim("roles", roles.stream().map(Role::getName).toList())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiry);
        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(Keys.hmacShaKeyFor(refreshSecretKey.getBytes()))
                .compact();
    }

    public String extractUsername(String token, boolean isRefresh) {
        byte[] keyBytes = (isRefresh ? refreshSecretKey : secretKey).getBytes();
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(keyBytes))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token, String username, boolean isRefresh) {
        String extractedUsername = extractUsername(token, isRefresh);
        return extractedUsername.equals(username) && !isTokenExpired(token, isRefresh);
    }

    private boolean isTokenExpired(String token, boolean isRefresh) {
        byte[] keyBytes = (isRefresh ? refreshSecretKey : secretKey).getBytes();
        Date expiration = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(keyBytes))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiration.before(new Date());
    }

    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("roles", List.class);    }

}
