package com.mariuszilinskas.vsp.users.profile.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    void uploadFile(String bucketName, String objectKey, MultipartFile file) throws IOException;

    void deleteFile(String objectKey, String bucketName);

}
