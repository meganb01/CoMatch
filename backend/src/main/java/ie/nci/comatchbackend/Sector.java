package ie.nci.comatchbackend;

import jakarta.persistence.*;

/**
 * JPA entity for the predefined sectors reference table (table: sectors).
 * Contains entries like "FinTech", "HealthTech", "AI", etc.
 */
@Entity
@Table(name = "sectors")
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sector_name", nullable = false, unique = true)
    private String sectorName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSectorName() { return sectorName; }
    public void setSectorName(String sectorName) { this.sectorName = sectorName; }
}
