package com.ccms.portal.util;

import com.ccms.portal.enums.TokenVariable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "12345678901234567890123456789012"); // 32 chars

        Field expirationField = JwtUtil.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, 3600000L); // 1 hour

        jwtUtil.init();
    }

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtUtil.generateToken("test@example.com", "USER", 1L);
        assertNotNull(token);
    }

    @Test
    void extractEmail_returnsCorrectEmail() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email, "USER", 1L);
        assertEquals(email, jwtUtil.extractEmail(token));
    }

    @Test
    void extractUserId_returnsCorrectId() {
        long id = 42L;
        String token = jwtUtil.generateToken("test@example.com", "USER", id);
        assertEquals(id, jwtUtil.extractUserId(token));
    }

    @Test
    void extractRole_returnsCorrectRole() {
        String role = "ADMIN";
        String token = jwtUtil.generateToken("test@example.com", role, 1L);
        assertEquals(role, jwtUtil.extractRole(token));
    }

    @Test
    void validateToken_returnsTrueForValidToken() {
        String token = jwtUtil.generateToken("test@example.com", "USER", 1L);
        assertTrue(jwtUtil.validateToken(token, "test@example.com"));
    }

    @Test
    void validateToken_returnsFalseForInvalidEmail() {
        String token = jwtUtil.generateToken("test@example.com", "USER", 1L);
        assertFalse(jwtUtil.validateToken(token, "other@example.com"));
    }

    @Test
    void validateToken_returnsFalseForExpiredToken() throws Exception {
        Field expirationField = JwtUtil.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, 1L);
        jwtUtil.init();
        String token = jwtUtil.generateToken("test@example.com", "USER", 1L);
        Thread.sleep(5);
        assertFalse(jwtUtil.validateToken(token, "test@example.com"));
    }

    @Test
    void generateRefreshToken_returnsNonNullToken() {
        String refreshToken = jwtUtil.generateRefreshToken("test@example.com", "USER", 1L);
        assertNotNull(refreshToken);
    }

    @Test
    void validateRefreshToken_returnsTrueForValidToken() {
        String refreshToken = jwtUtil.generateRefreshToken("test@example.com", "USER", 1L);
        assertTrue(jwtUtil.validateRefreshToken(refreshToken));
    }

    @Test
    void validateRefreshToken_returnsFalseForInvalidToken() {
        assertFalse(jwtUtil.validateRefreshToken("invalid.token"));
    }
}
