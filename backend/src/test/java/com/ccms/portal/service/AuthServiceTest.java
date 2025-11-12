package com.ccms.portal.service;

import com.ccms.portal.dto.request.LoginRequest;
import com.ccms.portal.dto.request.RegisterRequest;
import com.ccms.portal.dto.response.AuthResponse;
import com.ccms.portal.dto.response.UserResponse;
import com.ccms.portal.entity.UserEntity;
import com.ccms.portal.entity.UserProfileEntity;
import com.ccms.portal.enums.UserRole;
import com.ccms.portal.exception.EmailAlreadyExistsException;
import com.ccms.portal.exception.InvalidCredentialsException;
import com.ccms.portal.exception.UserNotFoundException;
import com.ccms.portal.repository.UserProfileRepository;
import com.ccms.portal.repository.UserRepository;
import com.ccms.portal.util.JwtUtil;
import com.ccms.portal.util.TokenblackList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenblackList tokenblackList;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // No additional setup needed with @ExtendWith and @InjectMocks
    }

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Anchal");
        request.setEmail("anchal@example.com");
        request.setPassword("securePass");
        request.setPhoneNumber("1234567890");
        request.setAddress("Bengaluru");
        request.setAnnualIncome(150000.0);

        when(userRepository.findByEmail("anchal@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("securePass")).thenReturn("hashedPass");

        UserEntity savedUser = new UserEntity();
        ReflectionTestUtils.setField(savedUser, "id", 1L);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        UserProfileEntity savedProfile = new UserProfileEntity();
        savedProfile.setEligibleBnpl(true);
        when(userProfileRepository.save(any(UserProfileEntity.class))).thenReturn(savedProfile);

        UserResponse response = authService.register(request);

        assertEquals("anchal@example.com", response.getEmail());
        assertTrue(response.getIsEligibleForBNPL());
    }

    @Test
    void register_emailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("anchal@example.com");

        when(userRepository.findByEmail("anchal@example.com")).thenReturn(Optional.of(new UserEntity()));

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("anchal@example.com", "securePass");

        UserEntity user = new UserEntity();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("anchal@example.com");
        user.setPasswordHash("hashedPass");
        user.setRole(UserRole.USER);

        UserProfileEntity profile = new UserProfileEntity();
        profile.setAddress("Bengaluru");

        when(userRepository.findByEmail("anchal@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("securePass", "hashedPass")).thenReturn(true);
        when(userProfileRepository.findByUser(user)).thenReturn(Optional.of(profile));
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any(), any(), any())).thenReturn("refresh-token");

        AuthResponse response = authService.login(request);

        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("anchal@example.com", response.getUser().getEmail());
    }

    @Test
    void login_userNotFound() {
        LoginRequest request = new LoginRequest("unknown@example.com", "pass");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void login_invalidPassword() {
        LoginRequest request = new LoginRequest("anchal@example.com", "wrongPass");

        UserEntity user = new UserEntity();
        user.setPasswordHash("hashedPass");

        when(userRepository.findByEmail("anchal@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "hashedPass")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void refreshAccessToken_success() {
        String oldRefreshToken = "old-refresh-token";

        when(tokenblackList.isBlacklisted(oldRefreshToken)).thenReturn(false);
        when(jwtUtil.validateRefreshToken(oldRefreshToken)).thenReturn(true);
        when(jwtUtil.extractEmail(oldRefreshToken)).thenReturn("anchal@example.com");

        UserEntity user = new UserEntity();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("anchal@example.com");
        user.setRole(UserRole.USER);

        UserProfileEntity profile = new UserProfileEntity();

        when(userRepository.findByEmail("anchal@example.com")).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUser(user)).thenReturn(Optional.of(profile));
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("new-access-token");
        when(jwtUtil.generateRefreshToken(any(), any(), any())).thenReturn("new-refresh-token");

        AuthResponse response = authService.refreshAccessToken(oldRefreshToken);

        assertEquals("new-access-token", response.getToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        verify(tokenblackList).blacklist(oldRefreshToken);
    }

    @Test
    void refreshAccessToken_blacklistedToken() {
        String token = "blacklisted-token";
        when(tokenblackList.isBlacklisted(token)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.refreshAccessToken(token));
    }

    @Test
    void refreshAccessToken_invalidToken() {
        String token = "invalid-token";
        when(tokenblackList.isBlacklisted(token)).thenReturn(false);
        when(jwtUtil.validateRefreshToken(token)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.refreshAccessToken(token));
    }
}
