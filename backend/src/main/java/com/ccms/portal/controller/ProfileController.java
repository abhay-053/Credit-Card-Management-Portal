package com.ccms.portal.controller;

import com.ccms.portal.dto.request.UpdateProfileRequest;
import com.ccms.portal.dto.response.UserResponse;
import com.ccms.portal.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{userId}")
    public UserResponse getProfile(@PathVariable Long userId) {
        log.info("Fetching profile for user ID: {}", userId);
        UserResponse response = profileService.getUserProfile(userId);
        log.debug("Profile fetched: {}", response);
        return response;
    }

    @PutMapping("/{userId}")
    public UserResponse updateProfile(@PathVariable Long userId,
                                      @RequestBody UpdateProfileRequest request) {
        log.info("Updating profile for user ID: {}", userId);
        log.debug("Update request payload: {}", request);
        UserResponse response = profileService.updateUserProfile(userId, request);
        log.debug("Updated profile response: {}", response);
        return response;
    }
}
