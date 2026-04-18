package ie.nci.comatchbackend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "swipes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"swiper_id", "target_id"}))
public class Swipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "swiper_id", nullable = false)
    private Long swiperId;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private String action; // "LIKE" or "PASS"

    public Swipe() {}

    public Swipe(Long swiperId, Long targetId, String action) {
        this.swiperId = swiperId;
        this.targetId = targetId;
        this.action = action;
    }

    public Long getId()           { return id; }
    public Long getSwiperId()     { return swiperId; }
    public Long getTargetId()     { return targetId; }
    public String getAction()     { return action; }
    public void setAction(String action) { this.action = action; }
}