package ie.nci.comatchbackend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for UserProfile entity.
 * ProfileService uses findByUserId to load or create the profile for a user.
 * DiscoverService uses findAllExceptUserId for the discover feed.
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUserId(Long userId);

    /** Find all profiles except the given user (for discover feed). */
    List<UserProfile> findByUserIdNot(Long userId);
}

