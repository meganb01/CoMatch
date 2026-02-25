package ie.nci.comatchbackend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for creating or updating a founder profile.
 * User sends JSON with name (required), bio, profilePhotoUrl, skills list, industry, country.
 */
public class ProfileRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Size(max = 500, message = "Bio must be at most 500 characters")
    private String bio;

    /** URL of profile photo (e.g. after upload to storage). */
    private String profilePhotoUrl;

    /** List of skill names; max 10 (enforced in ProfileService). */
    @Size(max = 10, message = "You can add at most 10 skills")
    private List<String> skills;

    /** Industry/sector interest (e.g. fintech, health-tech). */
    private String industry;

    /** Country or location. */
    private String country;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getBio() {

        return bio;
    }

    public void setBio(String bio) {

        this.bio = bio;
    }

    public String getProfilePhotoUrl() {

        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {

        this.profilePhotoUrl = profilePhotoUrl;
    }

    public List<String> getSkills() {

        return skills;
    }

    public void setSkills(List<String> skills) {

        this.skills = skills;
    }

    public String getIndustry() {

        return industry;
    }

    public void setIndustry(String industry) {

        this.industry = industry;
    }

    public String getCountry() {

        return country;
    }

    public void setCountry(String country) {

        this.country = country;
    }
}

