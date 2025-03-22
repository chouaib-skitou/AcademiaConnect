package com.academiaconnect.auth.authservice.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    // Inject secrets from application.properties (which in turn picks up the env variables)
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refreshSecret}")
    private String jwtRefreshSecret;

    private final long jwtExpirationInMs = 15 * 60 * 1000; // 15 minutes
    private final long jwtRefreshExpirationInMs = 7 * 24 * 60 * 60 * 1000; // 7 days

    private Key getSigningKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(Authentication authentication) {
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", authorities)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(jwtSecret), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(jwtRefreshSecret), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, jwtSecret);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, jwtRefreshSecret);
    }

    private boolean validateToken(String token, String secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(secret))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(jwtSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public java.util.Collection<? extends GrantedAuthority> getAuthorities(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(jwtSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();
        String roles = claims.get("roles", String.class);
        return java.util.Arrays.stream(roles.split(","))
                .map(role -> (GrantedAuthority) () -> role)
                .collect(Collectors.toList());
    }
}
