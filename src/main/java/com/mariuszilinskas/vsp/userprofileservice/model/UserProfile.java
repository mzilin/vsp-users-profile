package com.mariuszilinskas.vsp.userprofileservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * This entity describes a user profile within the platform.
 * Profiles are automatically removed when the associated user account is deleted.
 *
 * @author Marius Zilinskas
 */
@Entity
@Getter
@Setter
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "profile_name", nullable = false)
    private String profileName;

    @Column(name = "avatar_id", nullable = false)
    private UUID avatarId;

    @Column(name = "is_kid")
    private boolean isKid;

}
