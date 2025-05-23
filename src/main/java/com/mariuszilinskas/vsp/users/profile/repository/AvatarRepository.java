package com.mariuszilinskas.vsp.users.profile.repository;

import com.mariuszilinskas.vsp.users.profile.model.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing Avatar entities. Supports standard CRUD operations.
 *
 * @author Marius Zilinskas
 */
@Repository
public interface AvatarRepository extends JpaRepository<Avatar, UUID> {

    boolean existsByAvatarName(String avatarName);

}
