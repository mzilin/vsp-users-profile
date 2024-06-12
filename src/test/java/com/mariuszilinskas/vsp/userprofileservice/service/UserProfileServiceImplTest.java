package com.mariuszilinskas.vsp.userprofileservice.service;

import com.mariuszilinskas.vsp.userprofileservice.dto.CreateUserProfileRequest;
import com.mariuszilinskas.vsp.userprofileservice.exception.EntityExistsException;
import com.mariuszilinskas.vsp.userprofileservice.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.userprofileservice.model.Avatar;
import com.mariuszilinskas.vsp.userprofileservice.model.UserProfile;
import com.mariuszilinskas.vsp.userprofileservice.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository profileRepository;

    @Mock
    private AvatarService avatarService;

    @InjectMocks
    private UserProfileServiceImpl profileService;

    private final UUID userId = UUID.randomUUID();
    private final UUID profileId = UUID.randomUUID();
    private final UUID avatarId = UUID.randomUUID();
    private final Avatar avatar = new Avatar();
    private final UserProfile profile = new UserProfile();
    private final UserProfile profile2 = new UserProfile();
    private CreateUserProfileRequest createRequest;

    // ------------------------------------

    @BeforeEach
    void setUp() {
        avatar.setId(avatarId);

        profile.setId(profileId);
        profile.setUserId(userId);
        profile.setProfileName("Profile 1");
        profile.setAvatar(avatar);
        profile.setKid(false);

        profile2.setId(UUID.randomUUID());
        profile2.setUserId(userId);
        profile2.setProfileName("Profile 2");
        profile2.setAvatar(avatar);
        profile2.setKid(true);

        createRequest = new CreateUserProfileRequest(
                profile.getProfileName(),
                profile.getAvatar().getId(),
                profile.isKid()
        );
    }

    // ------------------------------------

    @Test
    void testCreateUserProfile_Success() {
        // Arrange
        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);

        when(profileRepository.existsByUserIdAndProfileName(userId, createRequest.profileName())).thenReturn(false);
        when(avatarService.getAvatar(createRequest.avatarId())).thenReturn(avatar);
        when(profileRepository.save(captor.capture())).thenReturn(profile);

        // Act
        UserProfile response = profileService.createUserProfile(userId, createRequest);

        // Assert
        assertNotNull(response);

        verify(profileRepository, times(1)).existsByUserIdAndProfileName(userId, createRequest.profileName());
        verify(avatarService, times(1)).getAvatar(avatarId);
        verify(profileRepository, times(1)).save(captor.capture());

        UserProfile savedProfile = captor.getValue();
        assertEquals(createRequest.profileName(), savedProfile.getProfileName());
        assertEquals(createRequest.avatarId(), savedProfile.getAvatar().getId());
        assertEquals(createRequest.isKid(), savedProfile.isKid());
    }

    @Test
    void testCreateUserProfile_NameExists() {
        // Arrange
        when(profileRepository.existsByUserIdAndProfileName(userId, createRequest.profileName())).thenReturn(true);

        // Act & Assert
        assertThrows(EntityExistsException.class, () -> profileService.createUserProfile(userId, createRequest));

        verify(profileRepository, times(1)).existsByUserIdAndProfileName(any(UUID.class), anyString());
        verify(avatarService, never()).getAvatar(any(UUID.class));
        verify(profileRepository, never()).save(any(UserProfile.class));
    }

    // ------------------------------------

    @Test
    void testGetAllUserProfiles_Success() {
        // Arrange
        List<UserProfile> avatars = List.of(profile, profile2);
        when(profileRepository.findAllByUserId(userId)).thenReturn(avatars);

        // Act
        List<UserProfile> response = profileService.getAllUserProfiles(userId);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(profile.getId(), response.get(0).getId());
        assertEquals(profile.getProfileName(), response.get(0).getProfileName());
        assertEquals(profile2.getId(), response.get(1).getId());
        assertEquals(profile2.getProfileName(), response.get(1).getProfileName());

        verify(profileRepository, times(1)).findAllByUserId(userId);
    }

    // ------------------------------------

    @Test
    void testGetUserProfile_Success() {
        // Arrange
        when(profileRepository.findByIdAndUserId(avatarId, userId)).thenReturn(Optional.of(profile));

        // Act
        UserProfile response = profileService.getUserProfile(userId, avatarId);

        // Assert
        assertNotNull(response);
        assertEquals(profile.getId(), response.getId());
        assertEquals(profile.getProfileName(), response.getProfileName());

        verify(profileRepository, times(1)).findByIdAndUserId(avatarId, userId);
    }

    @Test
    void testGetUserProfile_NonExistentUserProfile() {
        // Arrange
        UUID nonExistentProfileId = UUID.randomUUID();
        when(profileRepository.findByIdAndUserId(nonExistentProfileId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> profileService.getUserProfile(userId, nonExistentProfileId));

        // Assert
        verify(profileRepository, times(1)).findByIdAndUserId(nonExistentProfileId, userId);
    }

    // ------------------------------------
}
