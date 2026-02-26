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

    public FounderProfile(Long userId, String name, String bio, String profilePhotoUrl,
                          List<String> skills, String industry, String country) {
        this.userId = userId;
        this.name = name;
        this.bio = bio;
        this.profilePhotoUrl = profilePhotoUrl;
        this.skills = skills;
        this.industry = industry;
        this.country = country;
    }

    public Long getUserId() {
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

    public List<String> getSkills() {

        return skills;
    }

    public String getIndustry() {

        return industry;
    }

    public String getCountry() {

        return country;
    }
}

