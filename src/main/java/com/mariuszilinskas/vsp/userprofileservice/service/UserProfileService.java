package com.mariuszilinskas.vsp.userprofileservice.service;

import com.mariuszilinskas.vsp.userprofileservice.dto.CreateDefaultUserProfilesRequest;
import com.mariuszilinskas.vsp.userprofileservice.dto.CreateUserProfileRequest;
import com.mariuszilinskas.vsp.userprofileservice.model.UserProfile;

import java.util.List;
import java.util.UUID;

public interface UserProfileService {

    UserProfile createDefaultUserProfile(CreateDefaultUserProfilesRequest request);

    UserProfile createUserProfile(UUID userId, CreateUserProfileRequest request);

    List<UserProfile> getAllUserProfiles(UUID userId);

    UserProfile getUserProfile(UUID userId, UUID profileId);

    UserProfile updateUserProfile(UUID userId, UUID profileId, CreateUserProfileRequest request);

    void deleteUserProfile(UUID userId, UUID profileId);

    void deleteAllUserProfiles(UUID userId);

}
