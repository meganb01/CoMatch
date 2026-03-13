package ie.nci.comatchbackend;

import jakarta.persistence.*;

/**
 * JPA entity for the predefined skills reference table (table: skills).
 * Contains entries like "Software Development", "Marketing", "Finance", etc.
 */
@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skill_name", nullable = false, unique = true)
    private String skillName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
}
