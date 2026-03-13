package ie.nci.comatchbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the skills reference table and skill-name lookups via user_skills junction.
 */
public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findBySkillNameIgnoreCase(String skillName);

    @Query("SELECT s.skillName FROM Skill s JOIN UserSkill us ON us.skillId = s.id WHERE us.userId = :userId")
    List<String> findSkillNamesByUserId(@Param("userId") Long userId);
}
