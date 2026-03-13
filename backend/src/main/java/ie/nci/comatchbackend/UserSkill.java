package ie.nci.comatchbackend;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * JPA entity for user_skills junction table (user_id + skill_id).
 * Matches schema: composite PK, no id column.
 */
@Entity
@Table(name = "user_skills")
@IdClass(UserSkill.UserSkillId.class)
public class UserSkill {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getSkillId() { return skillId; }
    public void setSkillId(Long skillId) { this.skillId = skillId; }

    /** Composite key for user_skills. */
    public static class UserSkillId implements Serializable {
        private Long userId;
        private Long skillId;
        public UserSkillId() {}
        public UserSkillId(Long userId, Long skillId) {
            this.userId = userId;
            this.skillId = skillId;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserSkillId that)) return false;
            return java.util.Objects.equals(userId, that.userId) && java.util.Objects.equals(skillId, that.skillId);
        }
        @Override
        public int hashCode() {
            return java.util.Objects.hash(userId, skillId);
        }
    }
}
