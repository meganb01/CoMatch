package ie.nci.comatchbackend;

import java.util.List;

/**
 * Response DTO for the discover page (GET /api/profiles/discover).
 * Field names match what discover.js expects: id, name, avatarUrl, sector, skills, bio.
 */
public class DiscoverProfile {

    private Long id;
    private String name;
    private String avatarUrl;
    private String sector;
    private List<String> skills;
    private String bio;
    private String country;

    public DiscoverProfile(Long id, String name, String avatarUrl, String sector,
                           List<String> skills, String bio, String country) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.sector = sector;
        this.skills = skills;
        this.bio = bio;
        this.country = country;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getSector() { return sector; }
    public List<String> getSkills() { return skills; }
    public String getBio() { return bio; }
    public String getCountry() { return country; }
}
