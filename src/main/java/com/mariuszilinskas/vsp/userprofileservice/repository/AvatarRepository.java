package com.mariuszilinskas.vsp.userprofileservice.repository;

import com.mariuszilinskas.vsp.userprofileservice.model.Avatar;
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
