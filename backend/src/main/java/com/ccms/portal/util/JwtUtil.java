package com.ccms.portal.util;
import com.ccms.portal.enums.TokenVariable;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secret;

    @Value("${security.jwt.expiration-time:3600000}")
    private long expiration;

    private Key key;

    @PostConstruct
    public void init() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 characters)");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, String role,Long id) {
        return Jwts.builder()
                .setSubject(email)
                .claim(TokenVariable.USER_ROLE.getValue(), role)
                .claim(TokenVariable.USER_ID.getValue(),id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public Long extractUserId(String token){ return getClaims(token).get(TokenVariable.USER_ID.getValue(),Long.class); }

    public String extractRole(String token) {
        return getClaims(token).get(TokenVariable.USER_ROLE.getValue(), String.class);
    }

    public boolean validateToken(String token, String email) {
        try {
            return extractEmail(token).equals(email) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    public String generateRefreshToken(String email, String role, Long id) {

        long refreshExpiration = 7 * 24 * 60 * 60 * 1000; // 7 days in ms

        return Jwts.builder()
                .setSubject(email)
                .claim(TokenVariable.USER_ROLE.getValue(), role)
                .claim(TokenVariable.USER_ID.getValue(), id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean validateRefreshToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }



    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
