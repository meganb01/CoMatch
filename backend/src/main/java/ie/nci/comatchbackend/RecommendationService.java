package ie.nci.comatchbackend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FR-020: AI-powered co-founder recommendations.
 * Scores every other user against the logged-in user based on
 * shared skills, matching industry/sector, same country, and startup stage.
 * Returns results sorted by score descending.
 */
@Service
public class RecommendationService {

    private final UserProfileRepository userProfileRepository;
    private final ProfileService profileService;

    public RecommendationService(UserProfileRepository userProfileRepository,
                                  ProfileService profileService) {
        this.userProfileRepository = userProfileRepository;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> getRecommendations(Long userId) {
        FounderProfile myProfile = profileService.getProfile(userId);

        List<UserProfile> others = userProfileRepository.findByUserIdNot(userId);

        return others.stream()
                .map(up -> {
                    FounderProfile other = profileService.toFounderProfile(up);
                    int score = calculateScore(myProfile, other);
                    return new RecommendationResponse(other, score);
                })
                .filter(r -> r.getScore() > 0)
                .sorted(Comparator.comparingInt(RecommendationResponse::getScore).reversed())
                .collect(Collectors.toList());
    }

    int calculateScore(FounderProfile me, FounderProfile other) {
        int score = 0;

        Set<String> mySkills = normalize(me.getSkills());
        Set<String> theirSkills = normalize(other.getSkills());
        long common = mySkills.stream().filter(theirSkills::contains).count();
        score += (int) (common * 20);

        if (matches(me.getIndustry(), other.getIndustry())) {
            score += 30;
        }

        if (matches(me.getCountry(), other.getCountry())) {
            score += 10;
        }

        if (me.getStartupStage() != null && me.getStartupStage().equals(other.getStartupStage())) {
            score += 10;
        }

        return Math.min(score, 100);
    }

    private Set<String> normalize(List<String> items) {
        if (items == null) return Collections.emptySet();
        return items.stream()
                .filter(Objects::nonNull)
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toSet());
    }

    private boolean matches(String a, String b) {
        if (a == null || b == null || a.isBlank() || b.isBlank()) return false;
        return a.trim().equalsIgnoreCase(b.trim());
    }
}
