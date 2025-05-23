package com.mariuszilinskas.vsp.users.profile.service;

import com.mariuszilinskas.vsp.users.profile.dto.CreateUserDefaultProfileRequest;
import com.mariuszilinskas.vsp.users.profile.dto.CreateUserProfileRequest;
import com.mariuszilinskas.vsp.users.profile.exception.EntityExistsException;
import com.mariuszilinskas.vsp.users.profile.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.users.profile.model.Avatar;
import com.mariuszilinskas.vsp.users.profile.model.Profile;
import com.mariuszilinskas.vsp.users.profile.repository.ProfileRepository;
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
public class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private AvatarService avatarService;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private final UUID userId = UUID.randomUUID();
    private final UUID profileId = UUID.randomUUID();
    private final UUID avatarId = UUID.randomUUID();
    private final Avatar avatar = new Avatar();
    private final Profile profile = new Profile();
    private final Profile profile2 = new Profile();
    private CreateUserProfileRequest createRequest;
    private CreateUserDefaultProfileRequest createDefaultRequest;

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

        createDefaultRequest = new CreateUserDefaultProfileRequest(userId, "Name");

        createRequest = new CreateUserProfileRequest(
                profile.getProfileName(),
                profile.getAvatar().getId(),
                profile.isKid()
        );

    }

    // ------------------------------------

    @Test
    void testCreateDefaultUserProfile_Success() {
        // Arrange
        ArgumentCaptor<Profile> captor = ArgumentCaptor.forClass(Profile.class);

        when(avatarService.getRandomAvatar()).thenReturn(avatar);
        when(profileRepository.save(captor.capture())).thenReturn(profile);

        // Act
        Profile response = profileService.createDefaultUserProfile(createDefaultRequest);

        // Assert
        assertNotNull(response);

        verify(avatarService, times(1)).getRandomAvatar();
        verify(profileRepository, times(1)).save(captor.capture());

        Profile savedProfile = captor.getValue();
        assertEquals(createDefaultRequest.firstName(), savedProfile.getProfileName());
        assertEquals(avatarId, savedProfile.getAvatar().getId());
        assertEquals(false, savedProfile.isKid());
    }

    @Test
    void testCreateDefaultUserProfile_NoAvatarFound() {
        // Arrange
        ArgumentCaptor<Profile> captor = ArgumentCaptor.forClass(Profile.class);

        when(avatarService.getRandomAvatar()).thenReturn(null);
        when(profileRepository.save(captor.capture())).thenReturn(profile);

        // Act
        Profile response = profileService.createDefaultUserProfile(createDefaultRequest);

        // Assert
        assertNotNull(response);

        verify(avatarService, times(1)).getRandomAvatar();
        verify(profileRepository, times(1)).save(captor.capture());

        Profile savedProfile = captor.getValue();
        assertEquals(createDefaultRequest.firstName(), savedProfile.getProfileName());
        assertEquals(null, savedProfile.getAvatar());
        assertEquals(false, savedProfile.isKid());
    }

    // ------------------------------------

    @Test
    void testCreateUserProfile_Success() {
        // Arrange
        ArgumentCaptor<Profile> captor = ArgumentCaptor.forClass(Profile.class);

        when(profileRepository.existsByUserIdAndProfileName(userId, createRequest.profileName())).thenReturn(false);
        when(avatarService.getAvatar(createRequest.avatarId())).thenReturn(avatar);
        when(profileRepository.save(captor.capture())).thenReturn(profile);

        // Act
        Profile response = profileService.createUserProfile(userId, createRequest);

        // Assert
        assertNotNull(response);

        verify(profileRepository, times(1)).existsByUserIdAndProfileName(userId, createRequest.profileName());
        verify(avatarService, times(1)).getAvatar(avatarId);
        verify(profileRepository, times(1)).save(captor.capture());

        Profile savedProfile = captor.getValue();
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
        verify(profileRepository, never()).save(any(Profile.class));
    }

    // ------------------------------------

    @Test
    void testGetAllUserProfiles_Success() {
        // Arrange
        List<Profile> avatars = List.of(profile, profile2);
        when(profileRepository.findAllByUserId(userId)).thenReturn(avatars);

        // Act
        List<Profile> response = profileService.getAllUserProfiles(userId);

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
        Profile response = profileService.getUserProfile(userId, avatarId);

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

    @Test
    void testDeleteUserProfile_Success() {
        // Arrange
        when(profileRepository.findByIdAndUserId(profileId, userId)).thenReturn(Optional.of(profile));
        doNothing().when(profileRepository).delete(profile);

        // Act
        profileService.deleteUserProfile(userId, profileId);

        // Assert
        verify(profileRepository, times(1)).findByIdAndUserId(profileId, userId);
        verify(profileRepository, times(1)).delete(profile);

        when(profileRepository.findByIdAndUserId(profileId, userId)).thenReturn(Optional.empty());
        assertFalse(profileRepository.findByIdAndUserId(profileId, userId).isPresent());
    }

    @Test
    void testDeleteUserProfile_NonExistingProfile() {
        // Arrange
        UUID nonExistentAvatarId = UUID.randomUUID();
        when(profileRepository.findByIdAndUserId(nonExistentAvatarId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> profileService.deleteUserProfile(userId, nonExistentAvatarId));

        // Assert
        verify(profileRepository, times(1)).findByIdAndUserId(nonExistentAvatarId, userId);
        verify(profileRepository, never()).delete(any(Profile.class));
    }

    // ------------------------------------

    @Test
    void testDeleteAllUserProfiles_Success() {
        // Arrange
        doNothing().when(profileRepository).deleteAllByUserId(userId);

        // Act
        profileService.deleteAllUserProfiles(userId);

        // Assert
        verify(profileRepository, times(1)).deleteAllByUserId(userId);
    }

}
