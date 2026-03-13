package ie.nci.comatchbackend;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for UserProfile entity.
 * ProfileService uses findByUserId to load or create the profile for a user.
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUserId(Long userId);

    /**
     * All discoverable profiles: exclude current user and already-swiped users.
     */
    @Query(value = """
        SELECT p.* FROM user_profile p
        WHERE p.user_id != :excludeId
        AND p.user_id NOT IN (SELECT s.swiped_id FROM swipes s WHERE s.swiper_id = :excludeId)
        ORDER BY p.user_id
        """, nativeQuery = true)
    List<UserProfile> findAllDiscoverable(@Param("excludeId") Long excludeId);

    /**
     * Filtered candidates with pagination, starting after a given user_id.
     * Optional filters: country (location), industry (sector_name), skill (skill_name).
     */
    @Query(value = """
        SELECT DISTINCT p.* FROM user_profile p
        LEFT JOIN user_sectors us ON us.user_id = p.user_id
        LEFT JOIN sectors sec ON sec.id = us.sector_id
        LEFT JOIN user_skills usk ON usk.user_id = p.user_id
        LEFT JOIN skills sk ON sk.id = usk.skill_id
        WHERE p.user_id != :excludeId
        AND p.user_id > :afterId
        AND p.user_id NOT IN (SELECT s.swiped_id FROM swipes s WHERE s.swiper_id = :excludeId)
        AND (:country IS NULL OR :country = '' OR LOWER(p.location) = LOWER(:country))
        AND (:industry IS NULL OR :industry = '' OR LOWER(sec.sector_name) = LOWER(:industry))
        AND (:skill IS NULL OR :skill = '' OR LOWER(sk.skill_name) = LOWER(:skill))
        ORDER BY p.user_id
        """, nativeQuery = true)
    List<UserProfile> findFilteredCandidatesAfter(
            @Param("excludeId") Long excludeId,
            @Param("afterId") Long afterId,
            @Param("country") String country,
            @Param("industry") String industry,
            @Param("skill") String skill,
            Pageable pageable);

    /**
     * Filtered candidates with pagination (no "after" cursor).
     * Optional filters: country (location), industry (sector_name), skill (skill_name).
     */
    @Query(value = """
        SELECT DISTINCT p.* FROM user_profile p
        LEFT JOIN user_sectors us ON us.user_id = p.user_id
        LEFT JOIN sectors sec ON sec.id = us.sector_id
        LEFT JOIN user_skills usk ON usk.user_id = p.user_id
        LEFT JOIN skills sk ON sk.id = usk.skill_id
        WHERE p.user_id != :excludeId
        AND p.user_id NOT IN (SELECT s.swiped_id FROM swipes s WHERE s.swiper_id = :excludeId)
        AND (:country IS NULL OR :country = '' OR LOWER(p.location) = LOWER(:country))
        AND (:industry IS NULL OR :industry = '' OR LOWER(sec.sector_name) = LOWER(:industry))
        AND (:skill IS NULL OR :skill = '' OR LOWER(sk.skill_name) = LOWER(:skill))
        ORDER BY p.user_id
        """, nativeQuery = true)
    List<UserProfile> findFilteredCandidates(
            @Param("excludeId") Long excludeId,
            @Param("country") String country,
            @Param("industry") String industry,
            @Param("skill") String skill,
            Pageable pageable);
}

