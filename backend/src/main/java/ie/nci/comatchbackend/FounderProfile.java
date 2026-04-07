package ie.nci.comatchbackend;

import java.util.List;

/**
 * Response DTO for a founder profile.
 * Returned by GET /api/profile/me and POST /api/profile (after create/update).
 * Serialised to JSON for the frontend.
 */
public class FounderProfile {

    private Long userId;
    private String name;
    private String bio;
    private String profilePhotoUrl;
    private List<String> skills;
    private String industry;
    private String country;
    /** FR-009: IDEA, MVP, or FUNDED; may be null. */
    private String startupStage;

    public FounderProfile(Long userId, String name, String bio, String profilePhotoUrl,
                          List<String> skills, String industry, String country, String startupStage) {
        this.userId = userId;
        this.name = name;
        this.bio = bio;
        this.profilePhotoUrl = profilePhotoUrl;
        this.skills = skills;
        this.industry = industry;
        this.country = country;
        this.startupStage = startupStage;
    }

    public Long getUserId() {
        return userId;
    }

    /** Alias for frontend discover (expects profile.id). */
    public Long getId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getBio() {

        return bio;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    /** Alias so frontend discover.js can use profile.avatarUrl */
    public String getAvatarUrl() {
        return profilePhotoUrl;
    }

    public List<String> getSkills() {

        return skills;
    }

    public String getIndustry() {
        return industry;
    }

    /** Alias for frontend discover (expects profile.sector). */
    public String getSector() {
        return industry;
    }

    public String getCountry() {

        return country;
    }

    public String getStartupStage() {
        return startupStage;
    }
}

