package ie.nci.comatchbackend;

import java.util.List;

/**
 * DTO returned by GET /api/recommendations.
 * Wraps a FounderProfile with a compatibility score (0-100).
 */
public class RecommendationResponse {

    private Long userId;
    private String name;
    private String bio;
    private String profilePhotoUrl;
    private List<String> skills;
    private String industry;
    private String country;
    private String startupStage;
    private int score;

    public RecommendationResponse(FounderProfile profile, int score) {
        this.userId = profile.getUserId();
        this.name = profile.getName();
        this.bio = profile.getBio();
        this.profilePhotoUrl = profile.getProfilePhotoUrl();
        this.skills = profile.getSkills();
        this.industry = profile.getIndustry();
        this.country = profile.getCountry();
        this.startupStage = profile.getStartupStage();
        this.score = score;
    }

    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public List<String> getSkills() { return skills; }
    public String getIndustry() { return industry; }
    public String getCountry() { return country; }
    public String getStartupStage() { return startupStage; }
    public int getScore() { return score; }
}
