package ie.nci.comatchbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for UserProfile entity.
 * ProfileService uses findByUserId to load or create the profile for a user.
 * Discover uses filtered queries excluding self and already-swiped users.
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUserId(Long userId);

    /** Find all profiles except the given user (for discover feed). */
    List<UserProfile> findByUserIdNot(Long userId);

    /**
     * Discover: not self, not already swiped by current user, optional partial filters (null = ignore).
     */
    @Query("SELECT up FROM UserProfile up WHERE up.userId <> :curr "
            + "AND up.userId NOT IN (SELECT s.swipedId FROM Swipe s WHERE s.swiperId = :curr) "
            + "AND (:country IS NULL OR LOWER(COALESCE(up.country, '')) LIKE LOWER(CONCAT('%', :country, '%'))) "
            + "AND (:industry IS NULL OR LOWER(COALESCE(up.industry, '')) LIKE LOWER(CONCAT('%', :industry, '%'))) "
            + "AND (:skill IS NULL OR EXISTS (SELECT 1 FROM UserSkill us WHERE us.userId = up.userId "
            + "AND LOWER(us.skillName) LIKE LOWER(CONCAT('%', :skill, '%')))) "
            + "ORDER BY up.userId ASC")
    List<UserProfile> findDiscoverable(
            @Param("curr") Long currentUserId,
            @Param("country") String country,
            @Param("industry") String industry,
            @Param("skill") String skill);
}

