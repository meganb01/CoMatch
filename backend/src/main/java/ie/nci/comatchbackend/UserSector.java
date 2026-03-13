package ie.nci.comatchbackend;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * JPA entity for user_sectors junction table (user_id + sector_id).
 * Matches schema: composite PK, no id column.
 */
@Entity
@Table(name = "user_sectors")
@IdClass(UserSector.UserSectorId.class)
public class UserSector {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "sector_id", nullable = false)
    private Long sectorId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }

    /** Composite key for user_sectors. */
    public static class UserSectorId implements Serializable {
        private Long userId;
        private Long sectorId;
        public UserSectorId() {}
        public UserSectorId(Long userId, Long sectorId) {
            this.userId = userId;
            this.sectorId = sectorId;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserSectorId that)) return false;
            return java.util.Objects.equals(userId, that.userId) && java.util.Objects.equals(sectorId, that.sectorId);
        }
        @Override
        public int hashCode() {
            return java.util.Objects.hash(userId, sectorId);
        }
    }
}
