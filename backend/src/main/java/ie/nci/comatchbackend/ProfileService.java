package ie.nci.comatchbackend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ProfileService:  logic for founder profiles.
 * - createOrUpdateProfile: saves/updates UserProfile, replaces user's skills and sector in DB (single industry).
 * - getProfile: loads profile + skills + industry for a user.
 * Uses @Transactional so that delete + save of skills/sectors happen in one transaction.
 */
@Service
public class ProfileService {

    private static final int MAX_BIO_LENGTH = 500;
    private static final int MAX_SKILLS = 10;

    private final UserProfileRepository userProfileRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserSectorRepository userSectorRepository;

    public ProfileService(UserProfileRepository userProfileRepository,
                          UserSkillRepository userSkillRepository,
                          UserSectorRepository userSectorRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userSkillRepository = userSkillRepository;
        this.userSectorRepository = userSectorRepository;
    }

    /**
     * Create or update profile for the given user. Validates bio length and skills count.
     * Replaces all existing skills and sector for this user (delete then insert).
     */
    @Transactional
    public FounderProfile createOrUpdateProfile(Long userId, ProfileRequest request) {
        // Validate bio length
        String bio = Optional.ofNullable(request.getBio()).orElse("");
        if (bio.length() > MAX_BIO_LENGTH) {
            throw new IllegalArgumentException("Bio must be at most " + MAX_BIO_LENGTH + " characters");
        }

        List<String> skills = Optional.ofNullable(request.getSkills()).orElse(Collections.emptyList());
        if (skills.size() > MAX_SKILLS) {
            throw new IllegalArgumentException("A maximum of " + MAX_SKILLS + " skills is allowed per user");
        }

        // Load or create user profile row and update fields
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(UserProfile::new);
        profile.setUserId(userId);
        profile.setFullName(request.getName());
        profile.setBio(request.getBio());
        profile.setProfilePhotoUrl(request.getProfilePhotoUrl());
        profile.setIndustry(request.getIndustry());
        profile.setCountry(request.getCountry());
        userProfileRepository.save(profile);

        // Replace skills: delete existing, then insert from request
        userSkillRepository.deleteByUserId(userId);
        for (String skill : skills) {
            if (skill == null || skill.isBlank()) {
                continue;
            }
            UserSkill userSkill = new UserSkill();
            userSkill.setUserId(userId);
            userSkill.setSkillName(skill.trim());
            userSkillRepository.save(userSkill);
        }

        // Replace sector: we store a single industry per user in user_sectors
        userSectorRepository.deleteByUserId(userId);
        if (request.getIndustry() != null && !request.getIndustry().isBlank()) {
            UserSector sector = new UserSector();
            sector.setUserId(userId);
            sector.setSectorName(request.getIndustry().trim());
            userSectorRepository.save(sector);
        }

        List<String> savedSkills = userSkillRepository.findByUserId(userId)
                .stream()
                .map(UserSkill::getSkillName)
                .toList();

        String selectedIndustry = Optional.ofNullable(profile.getIndustry()).orElse(null);

        return new FounderProfile(
                userId,
                profile.getFullName(),
                profile.getBio(),
                profile.getProfilePhotoUrl(),
                savedSkills,
                selectedIndustry,
                profile.getCountry()
        );
    }

    /** Load profile for the given user; throws if profile not found. */
    @Transactional(readOnly = true)
    public FounderProfile getProfile(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user"));

        List<String> skills = userSkillRepository.findByUserId(userId)
                .stream()
                .map(UserSkill::getSkillName)
                .toList();

        String selectedIndustry = Optional.ofNullable(profile.getIndustry()).orElse(null);

        return new FounderProfile(
                userId,
                profile.getFullName(),
                profile.getBio(),
                profile.getProfilePhotoUrl(),
                skills,
                selectedIndustry,
                profile.getCountry()
        );
    }
}

