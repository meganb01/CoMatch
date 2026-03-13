package ie.nci.comatchbackend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Swipe entity.
 * Used by SwipeService to record and query swipe actions.
 */
public interface SwipeRepository extends JpaRepository<Swipe, Long> {

    Optional<Swipe> findBySwiperIdAndSwipedId(Long swiperId, Long swipedId);

    boolean existsBySwiperIdAndSwipedIdAndSwipeType(Long swiperId, Long swipedId, String swipeType);

    List<Swipe> findBySwiperId(Long swiperId);
}
