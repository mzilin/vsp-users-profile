package com.mariuszilinskas.vsp.userprofileservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.mariuszilinskas.vsp.userprofileservice.exception.FileUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AwsS3ServiceImplTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private AwsS3ServiceImpl awsS3Service;

    private MultipartFile multipartFile;
    private final String objectKey = "test-key";
    private final String bucketName = "bucket-name";

    // ------------------------------------

    @BeforeEach
    void setUp() {
        multipartFile = new MockMultipartFile(
                "file",
                "filename.jpg",
                "image/jpeg",
                "some content".getBytes()
        );
    }

    // ------------------------------------

    @Test
    void testUploadFile_Success() throws IOException {
        // Arrange
        PutObjectResult putObjectResult = new PutObjectResult();
        when(s3Client.putObject(anyString(), anyString(), any(), any(ObjectMetadata.class))).thenReturn(putObjectResult);

        // Act
        awsS3Service.uploadFile(bucketName, objectKey, multipartFile);

        // Assert
        verify(s3Client, times(1)).putObject(anyString(), anyString(), any(), any(ObjectMetadata.class));
    }

    @Test
    void testUploadFile_S3Exception() throws IOException {
        // Arrange
        AmazonS3Exception s3Exception = new AmazonS3Exception("S3 Error");
        s3Exception.setStatusCode(500);
        doThrow(s3Exception).when(s3Client).putObject(anyString(), anyString(), any(), any(ObjectMetadata.class));

        // Act & Assert
        assertThrows(FileUploadException.class, () -> awsS3Service.uploadFile(bucketName, objectKey, multipartFile));

        // Assert
        verify(s3Client, times(1)).putObject(anyString(), anyString(), any(), any(ObjectMetadata.class));
    }

    @Test
    void testUploadFile_IOException() throws IOException {
        // Arrange
        MultipartFile faultyFile = mock(MultipartFile.class);
        when(faultyFile.getInputStream()).thenThrow(new IOException("IO Error"));

        // Act & Assert
        assertThrows(FileUploadException.class, () -> awsS3Service.uploadFile(bucketName, objectKey, faultyFile));

        // Assert
        verify(s3Client, never()).putObject(anyString(), anyString(), any(), any(ObjectMetadata.class));
    }

    @Test
    void testDeleteFile_Success() {
        // Arrange
        doNothing().when(s3Client).deleteObject(anyString(), anyString());

        // Act
        awsS3Service.deleteFile(objectKey, bucketName);

        // Assert
        verify(s3Client, times(1)).deleteObject(anyString(), anyString());
    }

    @Test
    void testDeleteFile_S3Exception() {
        // Arrange
        AmazonS3Exception s3Exception = new AmazonS3Exception("S3 Error");
        s3Exception.setStatusCode(500);
        doThrow(s3Exception).when(s3Client).deleteObject(anyString(), anyString());

        // Act & Assert
        assertThrows(FileUploadException.class, () -> awsS3Service.deleteFile(objectKey, bucketName));

        // Assert
        verify(s3Client, times(1)).deleteObject(anyString(), anyString());
    }

}
