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
    private final SkillRepository skillRepository;
    private final SectorRepository sectorRepository;

    public ProfileService(UserProfileRepository userProfileRepository,
                          UserSkillRepository userSkillRepository,
                          UserSectorRepository userSectorRepository,
                          SkillRepository skillRepository,
                          SectorRepository sectorRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userSkillRepository = userSkillRepository;
        this.userSectorRepository = userSectorRepository;
        this.skillRepository = skillRepository;
        this.sectorRepository = sectorRepository;
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

        // Replace skills: delete existing, then insert from request (look up skill_id by name)
        userSkillRepository.deleteByUserId(userId);
        for (String skillName : skills) {
            if (skillName == null || skillName.isBlank()) continue;
            skillRepository.findBySkillNameIgnoreCase(skillName.trim()).ifPresent(skill -> {
                UserSkill userSkill = new UserSkill();
                userSkill.setUserId(userId);
                userSkill.setSkillId(skill.getId());
                userSkillRepository.save(userSkill);
            });
        }

        // Replace sector: we store a single industry per user in user_sectors (look up sector_id by name)
        userSectorRepository.deleteByUserId(userId);
        if (request.getIndustry() != null && !request.getIndustry().isBlank()) {
            sectorRepository.findBySectorNameIgnoreCase(request.getIndustry().trim()).ifPresent(sector -> {
                UserSector userSector = new UserSector();
                userSector.setUserId(userId);
                userSector.setSectorId(sector.getId());
                userSectorRepository.save(userSector);
            });
        }

        List<String> savedSkills = skillRepository.findSkillNamesByUserId(userId);

        String selectedIndustry = Optional.ofNullable(profile.getIndustry()).orElse(null);
        if (selectedIndustry == null) {
            selectedIndustry = sectorRepository.findSectorNamesByUserId(userId).stream()
                    .findFirst()
                    .orElse(null);
        }

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

        List<String> skills = skillRepository.findSkillNamesByUserId(userId);

        String selectedIndustry = Optional.ofNullable(profile.getIndustry()).orElse(null);
        if (selectedIndustry == null) {
            selectedIndustry = sectorRepository.findSectorNamesByUserId(userId).stream()
                    .findFirst()
                    .orElse(null);
        }

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

