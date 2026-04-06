package ie.nci.comatchbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for UserSkill entity.
 * deleteByUserId is used when replacing a user's skills on profile update (must be @Modifying + @Query).
 */
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    List<UserSkill> findByUserId(Long userId);

    /** Delete all skills for the user; call within a @Transactional method. */
    @Modifying
    @Query("DELETE FROM UserSkill u WHERE u.userId = :userId")
    void deleteByUserId(Long userId);
}

