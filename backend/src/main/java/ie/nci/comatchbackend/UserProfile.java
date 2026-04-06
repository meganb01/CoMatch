package ie.nci.comatchbackend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity: founder profile (table: user_profile).
 * One row per user; user_id is the primary key (same as users.user_id).
 * Skills and sector are stored in separate tables (UserSkill, UserSector).
 */
@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "industry")
    private String industry;

    @Column(name = "country")
    private String country;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

