package com.mariuszilinskas.vsp.userprofileservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "avatars")
/**
 * This entity represents avatars within the platform.
 * Avatars are created by the system administrators and can be assigned
 * to user profiles either by default or through user selection.
 * Each avatar has a name, and an associated image link.
 *
 * @author Marius Zilinskas
 */
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "avatar_name", nullable = false, unique = true)
    private String avatarName;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "image_link", nullable = false)
    private String imageLink;

}
