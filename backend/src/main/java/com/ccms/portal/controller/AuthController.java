package com.ccms.portal.controller;

import com.ccms.portal.dto.request.LoginRequest;
import com.ccms.portal.dto.request.RegisterRequest;
import com.ccms.portal.dto.response.AuthResponse;
import com.ccms.portal.dto.response.UserResponse;
import com.ccms.portal.service.AuthService;
import com.ccms.portal.util.TokenblackList;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final TokenblackList tokenBlacklist;

    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest request) {
        log.info("Registering user with email: {}", request.getEmail());
        UserResponse response = authService.register(request);
        log.debug("Registration successful for user ID: {}", response.getId());
        return response;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {
        log.info("Login attempt for email: {}", request.getEmail());
        AuthResponse authResponse = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        log.debug("Login successful, refresh token cookie set for user ID: {}", authResponse.getUser().getId());
        return authResponse;
    }
    @PostMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response,
                         @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        // Blacklist access token if present
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklist.blacklist(token);
        }

        // Blacklist refresh token if present
        if (refreshToken != null) {
            tokenBlacklist.blacklist(refreshToken);
        }

        // Delete the refresh cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // delete cookie
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return "Successfully logged out.";
    }




    @PostMapping("/refresh")
    public AuthResponse refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                     HttpServletResponse response) {
        if (refreshToken == null || tokenBlacklist.isBlacklisted(refreshToken)) {
            log.warn("Refresh token is missing or blacklisted");
            throw new RuntimeException("Invalid or blacklisted refresh token");
        }

        log.info("Refreshing access token using refresh token");
        AuthResponse authResponse = authService.refreshAccessToken(refreshToken);


        ResponseCookie cookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        log.debug("New refresh token cookie set for user ID: {}", authResponse.getUser().getId());

        return authResponse;
    }


    @GetMapping("/home")
    public String home() {
        log.info("Accessed home endpoint");
        return "Welcome to home page, ";
    }
}
 