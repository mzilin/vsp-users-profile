package com.mariuszilinskas.vsp.userprofileservice.service;

import com.mariuszilinskas.vsp.userprofileservice.dto.CreateAvatarRequest;
import com.mariuszilinskas.vsp.userprofileservice.exception.EntityExistsException;
import com.mariuszilinskas.vsp.userprofileservice.exception.IncorrectFileException;
import com.mariuszilinskas.vsp.userprofileservice.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.userprofileservice.model.Avatar;
import com.mariuszilinskas.vsp.userprofileservice.repository.AvatarRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service implementation for managing user avatars.
 * This service handles user creation, retrieval and deletion.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarServiceImpl.class);
    private final AvatarRepository avatarRepository;
    private final S3Service s3Service;

    @Value("${aws.s3.avatarBucketName}")
    private String avatarBucketName;

    @Value("${aws.s3.region}")
    private String region;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tiff", ".webp", ".svg"
    );

    @Override
    public Avatar createAvatar(CreateAvatarRequest request) throws IOException {
        logger.info("Creating Avatar: '{}", request.avatarName());
        checkNameExists(request.avatarName());
        String objectKey = generateObjectKey(request.file().getOriginalFilename());
        s3Service.uploadFile(avatarBucketName, objectKey, request.file());
        return populateNewAvatarWithRequestData(request, objectKey);
    }

    private void checkNameExists(String avatarName) {
        if (avatarRepository.existsByAvatarName(avatarName)) {
            throw new EntityExistsException(Avatar.class, "name", avatarName);
        }
    }

    private Avatar populateNewAvatarWithRequestData(CreateAvatarRequest request, String objectKey) {
        Avatar newAvatar = new Avatar();
        newAvatar.setAvatarName(request.avatarName());
        newAvatar.setObjectKey(objectKey);
        newAvatar.setImageLink(generateObjectUrl(objectKey));
        return avatarRepository.save(newAvatar);
    }

    private String generateObjectKey(String originalFileName) {
        String fileExtension = getFileExtension(originalFileName);
        return UUID.randomUUID() + fileExtension;
    }

    private String getFileExtension(String originalFileName) {
        if (originalFileName == null || originalFileName.lastIndexOf(".") == -1)
            throw new IncorrectFileException("No extension found");

        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

        if (ALLOWED_EXTENSIONS.contains(extension))
            return extension;

        throw new IncorrectFileException(extension);
    }

    private String generateObjectUrl(String objectKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", avatarBucketName, region, objectKey);
    }

    @Override
    public List<Avatar> getAvatars() {
        logger.info("Getting all Avatars");
        return avatarRepository.findAll();
    }

    @Override
    public Avatar getAvatar(UUID avatarId) {
        logger.info("Getting Avatar [id: '{}']", avatarId);
        return findAvatarById(avatarId);
    }

    @Override
    public void deleteAvatar(UUID avatarId) {
        logger.info("Deleting Avatar [id: '{}']", avatarId);
        Avatar avatar = findAvatarById(avatarId);
        s3Service.deleteFile(avatar.getObjectKey(), avatarBucketName);
        avatarRepository.delete(avatar);
    }

    private Avatar findAvatarById(UUID avatarId) {
        return avatarRepository.findById(avatarId)
                .orElseThrow(() -> new ResourceNotFoundException(Avatar.class, "id", avatarId));
    }
}
