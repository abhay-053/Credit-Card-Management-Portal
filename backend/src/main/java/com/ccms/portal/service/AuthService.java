package com.ccms.portal.service;

import com.ccms.portal.dto.request.LoginRequest;
import com.ccms.portal.dto.request.RegisterRequest;
import com.ccms.portal.dto.response.AuthResponse;
import com.ccms.portal.dto.response.UserResponse;
import com.ccms.portal.entity.UserEntity;
import com.ccms.portal.enums.UserRole;
import com.ccms.portal.entity.UserProfileEntity;
import com.ccms.portal.exception.EmailAlreadyExistsException;
import com.ccms.portal.exception.InvalidCredentialsException;
import com.ccms.portal.exception.UserNotFoundException;
import com.ccms.portal.repository.UserProfileRepository;
import com.ccms.portal.repository.UserRepository;
import com.ccms.portal.util.JwtUtil;
import com.ccms.portal.util.TokenblackList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenblackList tokenblackList;

    public UserResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed: email already exists - {}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already registered");
        }

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
        log.debug("User entity saved with ID: {}", user.getId());

        boolean isEligible = request.getAnnualIncome() != null && request.getAnnualIncome() > 100000;

        UserProfileEntity profile = UserProfileEntity.builder()
                .user(user)
                .address(request.getAddress())
                .annualIncome(request.getAnnualIncome())
                .isEligibleBnpl(isEligible)
                .build();

        userProfileRepository.save(profile);
        log.debug("User profile saved for user ID: {}", user.getId());

        return buildUserResponse(user, profile);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found - {}", request.getEmail());
                    return new UserNotFoundException("User not found");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Login failed: invalid credentials for email {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid credentials");
        }

        UserProfileEntity profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("Login failed: profile not found for user ID {}", user.getId());
                    return new UserNotFoundException("User profile not found");
                });

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().toString(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole().toString(), user.getId());

        log.debug("Login successful: tokens generated for user ID {}", user.getId());

        return new AuthResponse(accessToken, refreshToken, buildUserResponse(user, profile));
    }

    public AuthResponse refreshAccessToken(String refreshToken) {
        log.info("Attempting to refresh access token");

        if (tokenblackList.isBlacklisted(refreshToken)) {
            log.warn("Refresh failed: token is blacklisted");
            throw new RuntimeException("Blacklisted refresh token");
        }

        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            log.warn("Refresh failed: token is invalid or expired");
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String email = jwtUtil.extractEmail(refreshToken);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Refresh failed: user not found for email {}", email);
                    return new UserNotFoundException("User not found");
                });

        UserProfileEntity profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("Refresh failed: profile not found for user ID {}", user.getId());
                    return new UserNotFoundException("User profile not found");
                });

        tokenblackList.blacklist(refreshToken);
        log.debug("Old refresh token blacklisted for user ID {}", user.getId());

        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole().toString(), user.getId());
        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().toString(), user.getId());

        log.info("Access token refreshed successfully for user ID {}", user.getId());

        return new AuthResponse(newAccessToken, newRefreshToken, buildUserResponse(user, profile));
    }

    private UserResponse buildUserResponse(UserEntity user, UserProfileEntity profile) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .phoneNumber(user.getPhoneNumber())
                .address(profile.getAddress())
                .isEligibleForBNPL(profile.isEligibleBnpl())
                .build();
    }
}
