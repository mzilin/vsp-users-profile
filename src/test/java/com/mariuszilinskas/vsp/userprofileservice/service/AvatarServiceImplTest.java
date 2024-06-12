package com.mariuszilinskas.vsp.userprofileservice.service;

import com.mariuszilinskas.vsp.userprofileservice.dto.CreateAvatarRequest;
import com.mariuszilinskas.vsp.userprofileservice.exception.EntityExistsException;
import com.mariuszilinskas.vsp.userprofileservice.exception.IncorrectFileException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceImplTest {

    @Mock
    private AvatarRepository avatarRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private AvatarServiceImpl avatarService;

    private final Avatar avatar = new Avatar();
    private final Avatar avatar2 = new Avatar();
    private CreateAvatarRequest createRequest;

    // ------------------------------------

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField(avatarService, "avatarBucketName", "bucket-name");
        setPrivateField(avatarService, "region", "region-name");

        avatar.setAvatarName("Default");
        avatar2.setAvatarName("Kids");

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
        assertNotNull(savedAvatar.getImageLink());
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


}
