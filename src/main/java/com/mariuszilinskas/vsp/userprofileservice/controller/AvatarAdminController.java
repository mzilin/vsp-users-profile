package com.mariuszilinskas.vsp.userprofileservice.controller;

import com.mariuszilinskas.vsp.userprofileservice.dto.CreateAvatarRequest;
import com.mariuszilinskas.vsp.userprofileservice.model.Avatar;
import com.mariuszilinskas.vsp.userprofileservice.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * This class provides REST APIs for handling CRUD operations related to avatar management,
 * accessible to admins only.
 *
 * @author Marius Zilinskas
 */
@RestController
@RequestMapping("/admin/avatars")
@RequiredArgsConstructor
public class AvatarAdminController {

    private final AvatarService avatarService;

    @PostMapping
    public ResponseEntity<Avatar> createAvatar(
            @RequestPart("avatarName") String avatarName,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        CreateAvatarRequest request = new CreateAvatarRequest(avatarName, file);
        Avatar response =  avatarService.createAvatar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{avatarId}")
    public ResponseEntity<Void> deleteAvatar(@PathVariable UUID avatarId) {
        avatarService.deleteAvatar(avatarId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
