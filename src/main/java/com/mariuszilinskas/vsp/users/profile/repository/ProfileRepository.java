package com.mariuszilinskas.vsp.users.profile.repository;

import com.mariuszilinskas.vsp.users.profile.model.Profile;
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
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    boolean existsByUserIdAndProfileName(UUID userId, String profileName);

    boolean existsByUserIdAndProfileNameAndIdNot(UUID userId, String profileName, UUID id);

    List<Profile> findAllByUserId(UUID userId);

    Optional<Profile> findByIdAndUserId(UUID profileId, UUID userId);

    void deleteAllByUserId(UUID userId);

}
