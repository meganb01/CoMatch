package ie.nci.comatchbackend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * DiscoverService: logic for the discover feed and swipe actions.
 * - getDiscoverableProfiles: returns other users' profiles (excluding current user).
 * - When no other users exist, returns demo profiles for development (matches profile.js mock).
 * - swipe: persists LIKE/PASS; on reciprocal LIKEs, creates a row in {@code user_matches} and returns {@code match: true}.
 */
@Service
public class DiscoverService {

    private static final String LIKE = "LIKE";
    private static final String PASS = "PASS";

    private final UserProfileRepository userProfileRepository;
    private final ProfileService profileService;
    private final SwipeRepository swipeRepository;
    private final UserMatchRepository userMatchRepository;

    public DiscoverService(UserProfileRepository userProfileRepository,
                           ProfileService profileService,
                           SwipeRepository swipeRepository,
                           UserMatchRepository userMatchRepository) {
        this.userProfileRepository = userProfileRepository;
        this.profileService = profileService;
        this.swipeRepository = swipeRepository;
        this.userMatchRepository = userMatchRepository;
    }

    /**
     * Get discoverable profiles: excludes self and users already swiped by {@code currentUserId}.
     * Optional {@code country}, {@code industry}, {@code skill} are partial case-insensitive matches (null/blank = no filter).
     * When no filters are set, demo profiles pad the list if there are fewer than 3 real candidates (or none).
     */
    @Transactional(readOnly = true)
    public List<FounderProfile> getDiscoverableProfiles(Long currentUserId, String country, String industry, String skill) {
        String c = blankToNull(country);
        String i = blankToNull(industry);
        String s = blankToNull(skill);

        List<FounderProfile> real = userProfileRepository.findDiscoverable(currentUserId, c, i, s)
                .stream()
                .map(profileService::toFounderProfile)
                .toList();

        return real;
    }

    private static String blankToNull(String v) {
        if (v == null || v.isBlank()) {
            return null;
        }
        return v.trim();
    }

    /**
     * Records a swipe for real users. Demo profiles use negative ids and are not persisted.
     * Returns {@code match: true} when this swipe completes a mutual LIKE (new or existing match row).
     */
    @Transactional
    public SwipeResponse swipe(Long currentUserId, Long targetUserId, String action) {
        if (targetUserId == null) {
            throw new IllegalArgumentException("targetUserId is required");
        }
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot swipe on yourself");
        }

        String swipeType = "like".equalsIgnoreCase(action != null ? action : "pass") ? LIKE : PASS;

        Optional<Swipe> existingOpt = swipeRepository.findBySwiperIdAndSwipedId(currentUserId, targetUserId);
        if (existingOpt.isPresent()) {
            Swipe existing = existingOpt.get();
            if (PASS.equals(swipeType) && LIKE.equals(existing.getSwipeType())) {
                existing.setSwipeType(PASS);
                swipeRepository.save(existing);
            } else if (LIKE.equals(swipeType) && PASS.equals(existing.getSwipeType())) {
                existing.setSwipeType(LIKE);
                swipeRepository.save(existing);
            }
        } else {
            Swipe s = new Swipe();
            s.setSwiperId(currentUserId);
            s.setSwipedId(targetUserId);
            s.setSwipeType(swipeType);
            swipeRepository.save(s);
        }

        Swipe updated = swipeRepository.findBySwiperIdAndSwipedId(currentUserId, targetUserId).orElseThrow();

        if (!LIKE.equals(updated.getSwipeType())) {
            return new SwipeResponse(false);
        }

        boolean matched = ensureMutualMatch(currentUserId, targetUserId);
        return new SwipeResponse(matched);
    }

    /**
     * If both users have LIKE on each other, ensure a {@code user_matches} row exists. Returns whether a mutual match holds.
     */
    private boolean ensureMutualMatch(Long a, Long b) {
        Optional<Swipe> reverse = swipeRepository.findBySwiperIdAndSwipedId(b, a);
        if (reverse.isEmpty() || !LIKE.equals(reverse.get().getSwipeType())) {
            return false;
        }
        long u1 = Math.min(a, b);
        long u2 = Math.max(a, b);
        if (userMatchRepository.existsByUser1IdAndUser2Id(u1, u2)) {
            return true;
        }
        UserMatch m = new UserMatch();
        m.setUser1Id(u1);
        m.setUser2Id(u2);
        userMatchRepository.save(m);
        return true;
    }

    /** Response DTO for swipe endpoint. */
    public record SwipeResponse(boolean match) {}
}
