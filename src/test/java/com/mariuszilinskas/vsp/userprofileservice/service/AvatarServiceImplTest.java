package com.mariuszilinskas.vsp.userprofileservice.service;

import com.mariuszilinskas.vsp.userprofileservice.dto.CreateAvatarRequest;
import com.mariuszilinskas.vsp.userprofileservice.exception.EntityExistsException;
import com.mariuszilinskas.vsp.userprofileservice.exception.FileUploadException;
import com.mariuszilinskas.vsp.userprofileservice.exception.IncorrectFileException;
import com.mariuszilinskas.vsp.userprofileservice.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.userprofileservice.model.Avatar;
import com.mariuszilinskas.vsp.userprofileservice.repository.AvatarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceImplTest {

    @Mock
    private AvatarRepository avatarRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private AvatarServiceImpl avatarService;

    private final UUID avatarId = UUID.randomUUID();
    private final Avatar avatar = new Avatar();
    private final Avatar avatar2 = new Avatar();
    private CreateAvatarRequest createRequest;
    private MultipartFile multipartFile;

    // ------------------------------------

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField(avatarService, "avatarBucketName", "bucket-name");
        setPrivateField(avatarService, "region", "region-name");

        avatar.setId(avatarId);
        avatar.setAvatarName("Default");
        avatar.setObjectKey("test-avatar-key-1");
        avatar.setImageUrl("http://example.com/test-avatar-key-1.jpg");

        avatar2.setId(UUID.randomUUID());
        avatar2.setAvatarName("Kids");
        avatar2.setObjectKey("test-avatar-key-2");
        avatar2.setImageUrl("http://example.com/test-avatar-key-2.jpg");

        multipartFile = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some content".getBytes());
        createRequest = new CreateAvatarRequest(avatar.getAvatarName(), multipartFile);
    }

    // ------------------------------------

    private void setPrivateField(Object targetObject, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, value);
    }

    // ------------------------------------

    @Test
    void testCreateAvatar_Success() throws IOException {
        // Arrange
        ArgumentCaptor<Avatar> captor = ArgumentCaptor.forClass(Avatar.class);

        when(avatarRepository.existsByAvatarName(createRequest.avatarName())).thenReturn(false);
        doNothing().when(s3Service).uploadFile(anyString(), anyString(), eq(createRequest.file()));
        when(avatarRepository.save(captor.capture())).thenReturn(avatar);

        // Act
        Avatar response = avatarService.createAvatar(createRequest);

        // Assert
        assertNotNull(response);

        verify(avatarRepository, times(1)).existsByAvatarName(createRequest.avatarName());
        verify(s3Service, times(1)).uploadFile(anyString(), anyString(), eq(createRequest.file()));
        verify(avatarRepository, times(1)).save(captor.capture());

        Avatar savedAvatar = captor.getValue();
        assertEquals(createRequest.avatarName(), savedAvatar.getAvatarName());
        assertNotNull(savedAvatar.getObjectKey());
        assertNotNull(savedAvatar.getImageUrl());
    }

    @Test
    void testCreateAvatar_S3BucketError() throws IOException {
        // Arrange
        when(avatarRepository.existsByAvatarName(createRequest.avatarName())).thenReturn(false);
        doThrow(FileUploadException.class).when(s3Service).uploadFile(anyString(), anyString(), eq(createRequest.file()));

        // Act & Assert
        assertThrows(FileUploadException.class, () -> avatarService.createAvatar(createRequest));

        // Assert
        verify(avatarRepository, times(1)).existsByAvatarName(createRequest.avatarName());
        verify(s3Service, times(1)).uploadFile(anyString(), anyString(), eq(createRequest.file()));
        verify(avatarRepository, never()).save(any(Avatar.class));
    }

    @Test
    void testCreateAvatar_NameExists() throws IOException {
        // Arrange
        when(avatarRepository.existsByAvatarName(createRequest.avatarName())).thenReturn(true);

        // Act & Assert
        assertThrows(EntityExistsException.class, () -> avatarService.createAvatar(createRequest));

        verify(avatarRepository, times(1)).existsByAvatarName(createRequest.avatarName());
        verify(s3Service, never()).uploadFile(anyString(), anyString(), any(MultipartFile.class));
        verify(avatarRepository, never()).save(any(Avatar.class));
    }

    @Test
    void testCreateAvatar_IncorrectFile() throws IOException {
        // Arrange
        multipartFile = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());
        createRequest = new CreateAvatarRequest(avatar.getAvatarName(), multipartFile);

        when(avatarRepository.existsByAvatarName(createRequest.avatarName())).thenReturn(false);

        // Act & Assert
        assertThrows(IncorrectFileException.class, () -> avatarService.createAvatar(createRequest));

        verify(avatarRepository, times(1)).existsByAvatarName(createRequest.avatarName());
        verify(s3Service, never()).uploadFile(anyString(), anyString(), any(MultipartFile.class));
        verify(avatarRepository, never()).save(any(Avatar.class));
    }

    // ------------------------------------

    @Test
    void testGetAvatars_Success() {
        // Arrange
        List<Avatar> avatars = List.of(avatar, avatar2);
        when(avatarRepository.findAll()).thenReturn(avatars);

        // Act
        List<Avatar> response = avatarService.getAvatars();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(avatar.getId(), response.get(0).getId());
        assertEquals(avatar.getAvatarName(), response.get(0).getAvatarName());
        assertEquals(avatar2.getId(), response.get(1).getId());
        assertEquals(avatar2.getAvatarName(), response.get(1).getAvatarName());

        verify(avatarRepository, times(1)).findAll();
    }

    // ------------------------------------

    @Test
    void testGetAvatar_Success() {
        // Arrange
        when(avatarRepository.findById(avatarId)).thenReturn(Optional.of(avatar));

        // Act
        Avatar response = avatarService.getAvatar(avatarId);

        // Assert
        assertNotNull(response);
        assertEquals(avatar.getId(), response.getId());
        assertEquals(avatar.getAvatarName(), response.getAvatarName());

        verify(avatarRepository, times(1)).findById(avatarId);
    }

    @Test
    void testGetAvatar_NonExistentAvatar() {
        // Arrange
        UUID nonExistentAvatarId = UUID.randomUUID();
        when(avatarRepository.findById(nonExistentAvatarId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> avatarService.getAvatar(nonExistentAvatarId));

        // Assert
        verify(avatarRepository, times(1)).findById(nonExistentAvatarId);
    }

    // ------------------------------------

    @Test
    void testDeleteAvatar_Success() {
        // Arrange
        when(avatarRepository.findById(avatarId)).thenReturn(Optional.of(avatar));
        doNothing().when(s3Service).deleteFile(eq(avatar.getObjectKey()), anyString());
        doNothing().when(avatarRepository).delete(avatar);

        // Act
        avatarService.deleteAvatar(avatarId);

        // Assert
        verify(avatarRepository, times(1)).findById(avatarId);
        verify(s3Service, times(1)).deleteFile(eq(avatar.getObjectKey()), anyString());
        verify(avatarRepository, times(1)).delete(avatar);

        when(avatarRepository.findById(avatarId)).thenReturn(Optional.empty());
        assertFalse(avatarRepository.findById(avatarId).isPresent());
    }

    @Test
    void testDeleteAvatar_S3BucketError() {
        // Arrange
        when(avatarRepository.findById(avatarId)).thenReturn(Optional.of(avatar));
        doThrow(FileUploadException.class).when(s3Service).deleteFile(eq(avatar.getObjectKey()), anyString());

        // Act & Assert
        assertThrows(FileUploadException.class, () -> avatarService.deleteAvatar(avatarId));

        // Assert
        verify(avatarRepository, times(1)).findById(avatarId);
        verify(s3Service, times(1)).deleteFile(eq(avatar.getObjectKey()), anyString());
        verify(avatarRepository, never()).delete(avatar);

        when(avatarRepository.findById(avatarId)).thenReturn(Optional.of(avatar));
        assertTrue(avatarRepository.findById(avatarId).isPresent());
    }

    @Test
    void testDeleteAvatar_NonExistingToken() {
        // Arrange
        UUID nonExistentAvatarId = UUID.randomUUID();
        when(avatarRepository.findById(nonExistentAvatarId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> avatarService.deleteAvatar(nonExistentAvatarId));

        // Assert
        verify(avatarRepository, times(1)).findById(nonExistentAvatarId);
        verify(s3Service, never()).deleteFile(anyString(), anyString());
        verify(avatarRepository, never()).delete(any(Avatar.class));
    }

}
