package com.mariuszilinskas.vsp.users.profile.service;

import com.mariuszilinskas.vsp.users.profile.dto.CreateAvatarRequest;
import com.mariuszilinskas.vsp.users.profile.model.Avatar;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface AvatarService {

    Avatar createAvatar(CreateAvatarRequest request) throws IOException;

    List<Avatar> getAvatars();

    Avatar getAvatar(UUID avatarId);

    Avatar getRandomAvatar();

    void deleteAvatar(UUID avatarId);

}
