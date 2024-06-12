package com.mariuszilinskas.vsp.userprofileservice.controller;

import com.mariuszilinskas.vsp.userprofileservice.dto.CreateUserProfileRequest;
import com.mariuszilinskas.vsp.userprofileservice.model.UserProfile;
import com.mariuszilinskas.vsp.userprofileservice.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * This class provides REST APIs for handling CRUD operations related to user profiles.
 *
 * @author Marius Zilinskas
 */
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<UserProfile> createUserProfile(
            @Valid @RequestBody CreateUserProfileRequest request
    ) {
        UserProfile response = userProfileService.createUserProfile(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserProfile>> getAllUserProfiles(@PathVariable UUID userId) {
        List<UserProfile> response = userProfileService.getAllUserProfiles(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{profileId}")
    public ResponseEntity<UserProfile> getUserProfile(
            @PathVariable UUID userId,
            @PathVariable UUID profileId
    ) {
        UserProfile response = userProfileService.getUserProfile(userId, profileId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{userId}/{profileId}")
    public ResponseEntity<UserProfile> updateUserProfile(
            @PathVariable UUID userId,
            @PathVariable UUID profileId,
            @Valid @RequestBody CreateUserProfileRequest request
    ) {
        UserProfile response = userProfileService.updateUserProfile(userId, profileId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/{profileId}")
    public ResponseEntity<Void> deleteUserProfile(
            @PathVariable UUID userId,
            @PathVariable UUID profileId
    ) {
        userProfileService.deleteUserProfile(userId, profileId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
