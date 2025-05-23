package com.mariuszilinskas.vsp.users.profile.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.mariuszilinskas.vsp.users.profile.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service implementation for managing files in AWS S3.
 * This service handles file uploads and deletions in S3 buckets.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class AwsS3ServiceImpl implements S3Service {

    private static final Logger logger = LoggerFactory.getLogger(AwsS3ServiceImpl.class);
    private final AmazonS3 s3Client;

    @Override
    public void uploadFile(String bucketName, String objectKey, MultipartFile file) throws IOException {
        logger.info("Uploading file '{}' to s3 '{}' bucket", objectKey, bucketName);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            s3Client.putObject(bucketName, objectKey, file.getInputStream(), metadata);

        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                throw new FileUploadException("S3 object not found for: " + objectKey, e);
            } else throw new FileUploadException("AmazonS3 error occurred while uploading the file: " + e.getErrorMessage(), e);

        } catch (IOException e) {
            throw new FileUploadException("IO error: " + e.getMessage(), e);

        } catch (Exception e) {
            throw new FileUploadException("Error occurred while uploading file: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String objectKey, String bucketName) {
        logger.info("Deleting file '{}' from s3 '{}' bucket", objectKey, bucketName);

        try {
            s3Client.deleteObject(bucketName, objectKey);
        } catch (AmazonS3Exception e) {
            throw new FileUploadException("AmazonS3 error occurred while deleting the file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new FileUploadException("Error occurred while deleting the file: " + e.getMessage(), e);
        }
    }

}
