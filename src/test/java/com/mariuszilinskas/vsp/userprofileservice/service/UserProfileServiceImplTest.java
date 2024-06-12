package com.mariuszilinskas.vsp.userprofileservice.service;

import com.mariuszilinskas.vsp.userprofileservice.dto.CreateUserProfileRequest;
import com.mariuszilinskas.vsp.userprofileservice.exception.EntityExistsException;
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
}
