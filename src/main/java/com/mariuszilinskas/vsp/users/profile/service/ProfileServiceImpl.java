package com.mariuszilinskas.vsp.users.profile.service;

import com.mariuszilinskas.vsp.users.profile.dto.CreateUserDefaultProfileRequest;
import com.mariuszilinskas.vsp.users.profile.dto.CreateUserProfileRequest;
import com.mariuszilinskas.vsp.users.profile.exception.EntityExistsException;
import com.mariuszilinskas.vsp.users.profile.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.users.profile.model.Profile;
import com.mariuszilinskas.vsp.users.profile.repository.ProfileRepository;
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
public class ProfileServiceImpl implements ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);
    private final ProfileRepository profileRepository;
    private final AvatarService avatarService;

    @Override
    public Profile createDefaultUserProfile(CreateUserDefaultProfileRequest request) {
        logger.info("Creating default User Profile for User [userId: '{}']", request.userId());
        return createDefaultProfile(request);
    }

    private Profile createDefaultProfile(CreateUserDefaultProfileRequest request) {
        Profile profile = new Profile();
        profile.setUserId(request.userId());
        profile.setProfileName(request.firstName());
        profile.setAvatar(avatarService.getRandomAvatar());
        profile.setKid(false);
        return profileRepository.save(profile);
    }

    @Override
    public Profile createUserProfile(UUID userId, CreateUserProfileRequest request) {
        logger.info("Creating User Profile for User [userId: '{}']", userId);
        checkNameExists(userId, request.profileName());
        return populateNewUserProfileWithRequestData(userId, request);
    }

    private void checkNameExists(UUID userId, String profileName) {
        if (profileRepository.existsByUserIdAndProfileName(userId, profileName)) {
            throw new EntityExistsException(Profile.class, "name", profileName);
        }
    }

    private Profile populateNewUserProfileWithRequestData(UUID userId, CreateUserProfileRequest request) {
        Profile newProfile = new Profile();
        newProfile.setUserId(userId);
        return applyUserProfileUpdate(newProfile, request);
    }

    @Override
    public List<Profile> getAllUserProfiles(UUID userId) {
        logger.info("Getting All User Profiles for User [userId: '{}']", userId);
        return profileRepository.findAllByUserId(userId);
    }

    @Override
    public Profile getUserProfile(UUID userId, UUID profileId) {
        logger.info("Getting User Profile for User [userId: '{}']", userId);
        return findUserProfileByIdAndUserId(profileId, userId);
    }

    @Override
    public Profile updateUserProfile(UUID userId, UUID profileId, CreateUserProfileRequest request) {
        logger.info("Creating User Profile for User [userId: '{}']", userId);
        checkNameExists(userId, request.profileName(), profileId);
        Profile profile = findUserProfileByIdAndUserId(profileId, userId);
        return applyUserProfileUpdate(profile, request);
    }

    private void checkNameExists(UUID userId, String profileName, UUID profileId) {
        if (profileRepository.existsByUserIdAndProfileNameAndIdNot(userId, profileName, profileId)) {
            throw new EntityExistsException(Profile.class, "name", profileName);
        }
    }

    private Profile applyUserProfileUpdate(Profile profile, CreateUserProfileRequest request) {
        profile.setProfileName(request.profileName());
        profile.setAvatar(avatarService.getAvatar(request.avatarId()));
        profile.setKid(request.isKid());
        return profileRepository.save(profile);
    }

    @Override
    public void deleteUserProfile(UUID userId, UUID profileId) {
        logger.info("Deleting User Profile for User [userId: '{}']", userId);
        Profile profile = findUserProfileByIdAndUserId(profileId, userId);
        profileRepository.delete(profile);
    }

    private Profile findUserProfileByIdAndUserId(UUID profileId, UUID userId) {
        return profileRepository.findByIdAndUserId(profileId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(Profile.class, "id", profileId));
    }

    @Override
    public void deleteAllUserProfiles(UUID userId) {
        logger.info("Deleting All User Profiles for User [userId: '{}']", userId);
        profileRepository.deleteAllByUserId(userId);
    }

}
