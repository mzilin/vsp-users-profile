package com.mariuszilinskas.vsp.users.profile.service;

import com.mariuszilinskas.vsp.users.profile.dto.CreateUserDefaultProfileRequest;
import com.mariuszilinskas.vsp.users.profile.dto.CreateUserProfileRequest;
import com.mariuszilinskas.vsp.users.profile.model.Profile;

import java.util.List;
import java.util.UUID;

public interface ProfileService {

    Profile createDefaultUserProfile(CreateUserDefaultProfileRequest request);

    Profile createUserProfile(UUID userId, CreateUserProfileRequest request);

    List<Profile> getAllUserProfiles(UUID userId);

    Profile getUserProfile(UUID userId, UUID profileId);

    Profile updateUserProfile(UUID userId, UUID profileId, CreateUserProfileRequest request);

    void deleteUserProfile(UUID userId, UUID profileId);

    void deleteAllUserProfiles(UUID userId);

}
