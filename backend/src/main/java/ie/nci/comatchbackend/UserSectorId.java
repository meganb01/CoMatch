package ie.nci.comatchbackend;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for the user_sectors junction table (user_id + sector_id).
 */
public class UserSectorId implements Serializable {

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
        return Objects.equals(userId, that.userId) && Objects.equals(sectorId, that.sectorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, sectorId);
    }
}
