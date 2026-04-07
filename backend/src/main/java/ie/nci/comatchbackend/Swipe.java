package ie.nci.comatchbackend;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Records a single swipe (LIKE or PASS) from {@code swiperId} toward {@code swipedId}.
 */
@Entity
@Table(name = "swipes")
public class Swipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "swiper_id", nullable = false)
    private Long swiperId;

    @Column(name = "swiped_id", nullable = false)
    private Long swipedId;

    @Column(name = "swipe_type", nullable = false, length = 10)
    private String swipeType;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSwiperId() {
        return swiperId;
    }

    public void setSwiperId(Long swiperId) {
        this.swiperId = swiperId;
    }

    public Long getSwipedId() {
        return swipedId;
    }

    public void setSwipedId(Long swipedId) {
        this.swipedId = swipedId;
    }

    public String getSwipeType() {
        return swipeType;
    }

    public void setSwipeType(String swipeType) {
        this.swipeType = swipeType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
