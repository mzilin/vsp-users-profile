package com.mariuszilinskas.vsp.userprofileservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateUserProfileRequest(

        @NotBlank(message = "profileName cannot be blank")
        String profileName,

        @NotNull(message = "avatarId cannot be null")
        UUID avatarId,

        @NotNull(message = "isKid cannot be null")
        boolean isKid

){}
