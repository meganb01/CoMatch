package ie.nci.comatchbackend;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for the user_skills junction table (user_id + skill_id).
 */
public class UserSkillId implements Serializable {

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
        return Objects.equals(userId, that.userId) && Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, skillId);
    }
}
