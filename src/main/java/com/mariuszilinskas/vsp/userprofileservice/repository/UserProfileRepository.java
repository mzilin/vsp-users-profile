package com.mariuszilinskas.vsp.userprofileservice.repository;

import com.mariuszilinskas.vsp.userprofileservice.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing User Profile entities. Supports standard CRUD operations.
 *
 * @author Marius Zilinskas
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

}
