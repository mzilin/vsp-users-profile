package com.mariuszilinskas.vsp.users.profile.controller;

import com.mariuszilinskas.vsp.users.profile.model.Avatar;
import com.mariuszilinskas.vsp.users.profile.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This class provides REST APIs for handling CRUD operations related to avatars.
 *
 * @author Marius Zilinskas
 */
@RestController
@RequestMapping("/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @GetMapping
    public ResponseEntity<List<Avatar>> getAvatars() {
        List<Avatar> response =  avatarService.getAvatars();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
