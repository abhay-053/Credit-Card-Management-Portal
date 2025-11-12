package com.ccms.portal.service;

import com.ccms.portal.dto.request.UpdateProfileRequest;
import com.ccms.portal.dto.response.UserResponse;
import com.ccms.portal.entity.UserEntity;
import com.ccms.portal.entity.UserProfileEntity;
import com.ccms.portal.exception.UserNotFoundException;
import com.ccms.portal.repository.UserProfileRepository;
import com.ccms.portal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private ProfileService profileService;

    private UserEntity user;
    private UserProfileEntity profile;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("anchal@example.com");
        user.setName("Anchal");
        user.setPhoneNumber("1234567890");

        profile = new UserProfileEntity();
        profile.setAddress("Bengaluru");
        profile.setAnnualIncome(150000.0);
        profile.setEligibleBnpl(true);
        profile.setUser(user);
    }

    @Test
    void getUserProfile_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUser(user)).thenReturn(Optional.of(profile));

        UserResponse response = profileService.getUserProfile(1L);

        assertEquals("Anchal", response.getName());
        assertEquals("anchal@example.com", response.getEmail());
        assertEquals("Bengaluru", response.getAddress());
        assertTrue(response.getIsEligibleForBNPL());
    }

    @Test
    void getUserProfile_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> profileService.getUserProfile(99L));
    }

    @Test
    void getUserProfile_profileNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> profileService.getUserProfile(1L));
    }

    @Test
    void updateUserProfile_success() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Updated Name");
        request.setPhoneNumber("9876543210");
        request.setAddress("New Address");
        request.setAnnualIncome(200000.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUser(user)).thenReturn(Optional.of(profile));
        when(userRepository.save(user)).thenReturn(user);
        when(userProfileRepository.save(profile)).thenReturn(profile);

        UserResponse response = profileService.updateUserProfile(1L, request);

        assertEquals("Updated Name", response.getName());
        assertEquals("9876543210", response.getPhoneNumber());
        assertEquals("New Address", response.getAddress());
        assertTrue(response.getIsEligibleForBNPL());
    }

    @Test
    void updateUserProfile_userNotFound() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> profileService.updateUserProfile(99L, request));
    }

    @Test
    void updateUserProfile_profileNotFound() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> profileService.updateUserProfile(1L, request));
    }
}
