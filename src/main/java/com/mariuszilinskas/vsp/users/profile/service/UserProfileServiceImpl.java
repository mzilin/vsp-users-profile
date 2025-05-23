package com.mariuszilinskas.vsp.users.profile.service;

import com.mariuszilinskas.vsp.users.profile.dto.CreateUserDefaultProfileRequest;
import com.mariuszilinskas.vsp.users.profile.dto.CreateUserProfileRequest;
import com.mariuszilinskas.vsp.users.profile.exception.EntityExistsException;
import com.mariuszilinskas.vsp.users.profile.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.users.profile.model.UserProfile;
import com.mariuszilinskas.vsp.users.profile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing user profiles.
 * This service handles user creation, information updates, and deletion.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);
    private final UserProfileRepository profileRepository;
    private final AvatarService avatarService;

    @Override
    public UserProfile createDefaultUserProfile(CreateUserDefaultProfileRequest request) {
        logger.info("Creating default User Profile for User [userId: '{}']", request.userId());
        return createDefaultProfile(request);
    }

    private UserProfile createDefaultProfile(CreateUserDefaultProfileRequest request) {
        UserProfile profile = new UserProfile();
        profile.setUserId(request.userId());
        profile.setProfileName(request.firstName());
        profile.setAvatar(avatarService.getRandomAvatar());
        profile.setKid(false);
        return profileRepository.save(profile);
    }

    @Override
    public UserProfile createUserProfile(UUID userId, CreateUserProfileRequest request) {
        logger.info("Creating User Profile for User [userId: '{}']", userId);
        checkNameExists(userId, request.profileName());
        return populateNewUserProfileWithRequestData(userId, request);
    }

    private void checkNameExists(UUID userId, String profileName) {
        if (profileRepository.existsByUserIdAndProfileName(userId, profileName)) {
            throw new EntityExistsException(UserProfile.class, "name", profileName);
        }
    }

    private UserProfile populateNewUserProfileWithRequestData(UUID userId, CreateUserProfileRequest request) {
        UserProfile newProfile = new UserProfile();
        newProfile.setUserId(userId);
        return applyUserProfileUpdate(newProfile, request);
    }

    @Override
    public List<UserProfile> getAllUserProfiles(UUID userId) {
        logger.info("Getting All User Profiles for User [userId: '{}']", userId);
        return profileRepository.findAllByUserId(userId);
    }

    @Override
    public UserProfile getUserProfile(UUID userId, UUID profileId) {
        logger.info("Getting User Profile for User [userId: '{}']", userId);
        return findUserProfileByIdAndUserId(profileId, userId);
    }

    @Override
    public UserProfile updateUserProfile(UUID userId, UUID profileId, CreateUserProfileRequest request) {
        logger.info("Creating User Profile for User [userId: '{}']", userId);
        checkNameExists(userId, request.profileName(), profileId);
        UserProfile profile = findUserProfileByIdAndUserId(profileId, userId);
        return applyUserProfileUpdate(profile, request);
    }

    private void checkNameExists(UUID userId, String profileName, UUID profileId) {
        if (profileRepository.existsByUserIdAndProfileNameAndIdNot(userId, profileName, profileId)) {
            throw new EntityExistsException(UserProfile.class, "name", profileName);
        }
    }

    private UserProfile applyUserProfileUpdate(UserProfile profile, CreateUserProfileRequest request) {
        profile.setProfileName(request.profileName());
        profile.setAvatar(avatarService.getAvatar(request.avatarId()));
        profile.setKid(request.isKid());
        return profileRepository.save(profile);
    }

    @Override
    public void deleteUserProfile(UUID userId, UUID profileId) {
        logger.info("Deleting User Profile for User [userId: '{}']", userId);
        UserProfile profile = findUserProfileByIdAndUserId(profileId, userId);
        profileRepository.delete(profile);
    }

    private UserProfile findUserProfileByIdAndUserId(UUID profileId, UUID userId) {
        return profileRepository.findByIdAndUserId(profileId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(UserProfile.class, "id", profileId));
    }

    @Override
    public void deleteAllUserProfiles(UUID userId) {
        logger.info("Deleting All User Profiles for User [userId: '{}']", userId);
        profileRepository.deleteAllByUserId(userId);
    }

}
