package ie.nci.comatchbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for UserSector entity.
 * deleteByUserId is used when replacing a user's sector on profile update (must be @Modifying + @Query).
 */
public interface UserSectorRepository extends JpaRepository<UserSector, Long> {

    List<UserSector> findByUserId(Long userId);

    /** Delete all sectors for the user; call within a @Transactional method. */
    @Modifying
    @Query("DELETE FROM UserSector u WHERE u.userId = :userId")
    void deleteByUserId(Long userId);
}

