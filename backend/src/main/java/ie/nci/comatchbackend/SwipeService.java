package ie.nci.comatchbackend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * SwipeService: business logic for swiping (FR-011) and mutual match detection (FR-012).
 * When user A likes user B AND user B already liked user A, a Match row is created automatically.
 */
@Service
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final UserMatchRepository userMatchRepository;

    public SwipeService(SwipeRepository swipeRepository, UserMatchRepository userMatchRepository) {
        this.swipeRepository = swipeRepository;
        this.userMatchRepository = userMatchRepository;
    }

    @Transactional
    public SwipeResponse recordSwipe(Long swiperId, Long targetUserId, String swipeType) {
        String type = swipeType.toUpperCase();
        if (!"LIKE".equals(type) && !"PASS".equals(type)) {
            throw new IllegalArgumentException("swipeType must be LIKE or PASS");
        }
        if (swiperId.equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot swipe on yourself");
        }

        Optional<Swipe> existing = swipeRepository.findBySwiperIdAndSwipedId(swiperId, targetUserId);
        Swipe swipe;
        if (existing.isPresent()) {
            swipe = existing.get();
            swipe.setSwipeType(type);
        } else {
            swipe = new Swipe();
            swipe.setSwiperId(swiperId);
            swipe.setSwipedId(targetUserId);
            swipe.setSwipeType(type);
        }
        swipeRepository.save(swipe);

        boolean matched = false;
        Long matchId = null;

        if ("LIKE".equals(type)) {
            boolean otherLiked = swipeRepository.existsBySwiperIdAndSwipedIdAndSwipeType(
                    targetUserId, swiperId, "LIKE");

            if (otherLiked && !userMatchRepository.existsBetweenUsers(swiperId, targetUserId)) {
                Long user1 = Math.min(swiperId, targetUserId);
                Long user2 = Math.max(swiperId, targetUserId);
                UserMatch match = new UserMatch();
                match.setUser1Id(user1);
                match.setUser2Id(user2);
                UserMatch saved = userMatchRepository.save(match);
                matched = true;
                matchId = saved.getId();
            }
        }

        return new SwipeResponse(true, matched, matchId);
    }
}
