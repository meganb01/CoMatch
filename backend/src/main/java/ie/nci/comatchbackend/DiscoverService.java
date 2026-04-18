package ie.nci.comatchbackend;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SwipeRepository swipeRepository;
    private final MatchRepository matchRepository;

    public DiscoverService(UserProfileRepository userProfileRepository,
                           ProfileService profileService,
                           SwipeRepository swipeRepository,
                           MatchRepository matchRepository) {
        this.userProfileRepository = userProfileRepository;
        this.profileService = profileService;
        this.swipeRepository = swipeRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public List<FounderProfile> getDiscoverableProfiles(Long currentUserId) {
        List<FounderProfile> real = userProfileRepository.findByUserIdNot(currentUserId)
                .stream()
                .map(profileService::toFounderProfile)
                .toList();
        List<FounderProfile> combined =
            real.isEmpty() ? DEMO_PROFILES
            : real.size() >= 3 ? real
            : concat(real, DEMO_PROFILES);

        return combined.stream()
            .filter(profile -> !profile.getUserId().equals(currentUserId))
            .toList();
    }

    private List<FounderProfile> concat(List<FounderProfile> a, List<FounderProfile> b) {
        return java.util.stream.Stream.concat(a.stream(), b.stream()).toList();
    }

    @Transactional
    public SwipeResponse swipe(Long currentUserId, Long targetUserId, String action) {
        // Normalise action to uppercase so "like"/"LIKE" both work
        String normAction = action.toUpperCase();

        // Persist or update the swipe row
        Optional<Swipe> existing = swipeRepository.findBySwiperIdAndTargetId(currentUserId, targetUserId);
        if (existing.isPresent()) {
            existing.get().setAction(normAction);
            swipeRepository.save(existing.get());
        } else {
            swipeRepository.save(new Swipe(currentUserId, targetUserId, normAction));
        }

        // Check for mutual match
        boolean isMatch = false;
        if ("LIKE".equals(normAction)) {
            Optional<Swipe> reverse = swipeRepository
                    .findBySwiperIdAndTargetId(targetUserId, currentUserId);
            if (reverse.isPresent() && "LIKE".equals(reverse.get().getAction())) {
                // Create match row only if one doesn't already exist
                if (matchRepository.findByBothUsers(currentUserId, targetUserId).isEmpty()) {
                    matchRepository.save(new Match(currentUserId, targetUserId));
                }
                isMatch = true;
            }
        } else if ("PASS".equals(normAction)) {
            // Downgrade: remove existing match if present
            matchRepository.findByBothUsers(currentUserId, targetUserId)
                    .ifPresent(matchRepository::delete);
        }

        return new SwipeResponse(isMatch);
    }

    public record SwipeResponse(boolean match) {}
}