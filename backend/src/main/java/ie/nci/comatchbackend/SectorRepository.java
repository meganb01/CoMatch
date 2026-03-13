package ie.nci.comatchbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the sectors reference table and sector-name lookups via user_sectors junction.
 */
public interface SectorRepository extends JpaRepository<Sector, Long> {

    Optional<Sector> findBySectorNameIgnoreCase(String sectorName);

    @Query("SELECT s.sectorName FROM Sector s JOIN UserSector us ON us.sectorId = s.id WHERE us.userId = :userId")
    List<String> findSectorNamesByUserId(@Param("userId") Long userId);
}
