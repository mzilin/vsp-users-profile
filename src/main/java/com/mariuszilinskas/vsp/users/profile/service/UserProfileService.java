package com.mariuszilinskas.vsp.users.profile.service;

import com.mariuszilinskas.vsp.users.profile.dto.CreateUserDefaultProfileRequest;
import com.mariuszilinskas.vsp.users.profile.dto.CreateUserProfileRequest;
import com.mariuszilinskas.vsp.users.profile.model.UserProfile;

import java.util.List;
import java.util.UUID;

public interface UserProfileService {

    UserProfile createDefaultUserProfile(CreateUserDefaultProfileRequest request);

    UserProfile createUserProfile(UUID userId, CreateUserProfileRequest request);

    List<UserProfile> getAllUserProfiles(UUID userId);

    UserProfile getUserProfile(UUID userId, UUID profileId);

    UserProfile updateUserProfile(UUID userId, UUID profileId, CreateUserProfileRequest request);

    void deleteUserProfile(UUID userId, UUID profileId);

    void deleteAllUserProfiles(UUID userId);

}
