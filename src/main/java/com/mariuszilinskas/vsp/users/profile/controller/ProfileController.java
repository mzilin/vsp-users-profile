package com.mariuszilinskas.vsp.users.profile.controller;

import com.mariuszilinskas.vsp.users.profile.dto.CreateUserProfileRequest;
import com.mariuszilinskas.vsp.users.profile.model.Profile;
import com.mariuszilinskas.vsp.users.profile.service.ProfileService;
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
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/{userId}")
    public ResponseEntity<Profile> createUserProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateUserProfileRequest request
    ) {
        Profile response = profileService.createUserProfile(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Profile>> getAllUserProfiles(@PathVariable UUID userId) {
        List<Profile> response = profileService.getAllUserProfiles(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{profileId}")
    public ResponseEntity<Profile> getUserProfile(
            @PathVariable UUID userId,
            @PathVariable UUID profileId
    ) {
        Profile response = profileService.getUserProfile(userId, profileId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{userId}/{profileId}")
    public ResponseEntity<Profile> updateUserProfile(
            @PathVariable UUID userId,
            @PathVariable UUID profileId,
            @Valid @RequestBody CreateUserProfileRequest request
    ) {
        Profile response = profileService.updateUserProfile(userId, profileId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/{profileId}")
    public ResponseEntity<Void> deleteUserProfile(
            @PathVariable UUID userId,
            @PathVariable UUID profileId
    ) {
        profileService.deleteUserProfile(userId, profileId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteAllUserProfiles(@PathVariable UUID userId) {
        profileService.deleteAllUserProfiles(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
