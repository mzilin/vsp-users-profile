package com.mariuszilinskas.vsp.userprofileservice.service;

import com.mariuszilinskas.vsp.userprofileservice.dto.CreateAvatarRequest;
import com.mariuszilinskas.vsp.userprofileservice.model.Avatar;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface AvatarService {

    Avatar createAvatar(CreateAvatarRequest request) throws IOException;

    List<Avatar> getAvatars();

    Avatar getAvatar(UUID avatarId);

    void deleteAvatar(UUID avatarId);

}
