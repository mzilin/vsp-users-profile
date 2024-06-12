package com.mariuszilinskas.vsp.userprofileservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateAvatarRequest(

        @NotBlank(message = "avatarName cannot be blank")
        String avatarName,

        @NotNull(message = "file cannot be null")
        MultipartFile file

){}
