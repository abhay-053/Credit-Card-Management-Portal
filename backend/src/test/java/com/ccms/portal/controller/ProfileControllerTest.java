package com.ccms.portal.controller;

import com.ccms.portal.dto.request.UpdateProfileRequest;
import com.ccms.portal.dto.response.UserResponse;
import com.ccms.portal.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    @Test
    void getProfile_success() {
        Long userId = 1L;

        UserResponse expected = UserResponse.builder()
                .id(userId)
                .name("Anchal")
                .email("anchal@example.com")
                .address("Bengaluru")
                .isEligibleForBNPL(true)
                .build();

        when(profileService.getUserProfile(userId)).thenReturn(expected);

        UserResponse actual = profileController.getProfile(userId);

        assertNotNull(actual);
        assertEquals("Anchal", actual.getName());
        assertEquals("anchal@example.com", actual.getEmail());
        assertTrue(actual.getIsEligibleForBNPL());
    }

    @Test
    void updateProfile_success() {
        Long userId = 1L;

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .name("Updated Name")
                .phoneNumber("9876543210")
                .address("New Address")
                .annualIncome(200000.0)
                .build();

        UserResponse expected = UserResponse.builder()
                .id(userId)
                .name("Updated Name")
                .phoneNumber("9876543210")
                .address("New Address")
                .isEligibleForBNPL(true)
                .build();

        when(profileService.updateUserProfile(userId, request)).thenReturn(expected);

        UserResponse actual = profileController.updateProfile(userId, request);

        assertNotNull(actual);
        assertEquals("Updated Name", actual.getName());
        assertEquals("9876543210", actual.getPhoneNumber());
        assertEquals("New Address", actual.getAddress());
        assertTrue(actual.getIsEligibleForBNPL());
    }

    @Test
    void getProfile_userNotFound() {
        Long userId = 99L;

        when(profileService.getUserProfile(userId)).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> profileController.getProfile(userId));
    }

    @Test
    void updateProfile_userNotFound() {
        Long userId = 99L;
        UpdateProfileRequest request = new UpdateProfileRequest();

        when(profileService.updateUserProfile(userId, request)).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> profileController.updateProfile(userId, request));
    }
}
