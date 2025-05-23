package com.mariuszilinskas.vsp.users.profile.repository;

import com.mariuszilinskas.vsp.users.profile.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing User Profile entities. Supports standard CRUD operations.
 *
 * @author Marius Zilinskas
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    boolean existsByUserIdAndProfileName(UUID userId, String profileName);

    boolean existsByUserIdAndProfileNameAndIdNot(UUID userId, String profileName, UUID id);

    List<UserProfile> findAllByUserId(UUID userId);

    Optional<UserProfile> findByIdAndUserId(UUID profileId, UUID userId);

    void deleteAllByUserId(UUID userId);

}
