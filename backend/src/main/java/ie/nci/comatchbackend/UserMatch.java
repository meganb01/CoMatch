package ie.nci.comatchbackend;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity for mutual matches (table: matches).
 * Created automatically when two users both swipe LIKE on each other.
 * user1_id is always the smaller userId to avoid duplicate rows.
 */
@Entity
@Table(name = "matches")
public class UserMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user1_id", nullable = false)
    private Long user1Id;

    @Column(name = "user2_id", nullable = false)
    private Long user2Id;

    @Column(name = "matched_at", insertable = false, updatable = false)
    private LocalDateTime matchedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUser1Id() { return user1Id; }
    public void setUser1Id(Long user1Id) { this.user1Id = user1Id; }

    public Long getUser2Id() { return user2Id; }
    public void setUser2Id(Long user2Id) { this.user2Id = user2Id; }

    public LocalDateTime getMatchedAt() { return matchedAt; }
}
