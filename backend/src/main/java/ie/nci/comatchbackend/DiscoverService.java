package ie.nci.comatchbackend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DiscoverService: logic for the discover feed and swipe actions.
 * - getDiscoverableProfiles: returns other users' profiles (excluding current user).
 * - When no other users exist, returns demo profiles for development (matches profile.js mock).
 * - swipe: records like/pass (MVP: no persistence, returns match: false).
 */
@Service
public class DiscoverService {

    private static final List<FounderProfile> DEMO_PROFILES = List.of(
            new FounderProfile(-1L, "Alex Chen", "Looking for a technical co-founder.",
                    "https://i.pravatar.cc/150?img=12", List.of("Product Management", "Marketing"), "Tech, AI", "Ireland"),
            new FounderProfile(-2L, "Sarah Murphy", "Experienced in fintech. Seeking a product-minded co-founder.",
                    "https://i.pravatar.cc/150?img=5", List.of("Finance", "Compliance"), "FinTech", "Ireland"),
            new FounderProfile(-3L, "James O'Brien", "Full-stack developer looking for a business partner.",
                    "https://i.pravatar.cc/150?img=8", List.of("Development", "Design"), "HealthTech", "Ireland")
    );

    private final UserProfileRepository userProfileRepository;
    private final ProfileService profileService;

    public DiscoverService(UserProfileRepository userProfileRepository,
                           ProfileService profileService) {
        this.userProfileRepository = userProfileRepository;
        this.profileService = profileService;
    }

    /** Get discoverable profiles. Always appends demo profiles so there are at least 3 to swipe. */
    @Transactional(readOnly = true)
    public List<FounderProfile> getDiscoverableProfiles(Long currentUserId) {
        List<FounderProfile> real = userProfileRepository.findByUserIdNot(currentUserId)
                .stream()
                .map(profileService::toFounderProfile)
                .toList();
        return real.isEmpty() ? DEMO_PROFILES : real.size() >= 3 ? real : concat(real, DEMO_PROFILES);
    }

    private List<FounderProfile> concat(List<FounderProfile> a, List<FounderProfile> b) {
        return java.util.stream.Stream.concat(a.stream(), b.stream()).toList();
    }

    /** Process a swipe (like or pass). MVP: always returns match=false. */
    public SwipeResponse swipe(Long currentUserId, Long targetUserId, String action) {
        return new SwipeResponse(false);
    }

    /** Response DTO for swipe endpoint. */
    public record SwipeResponse(boolean match) {}
}
