package ie.nci.comatchbackend;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SwipeRepository extends JpaRepository<Swipe, Long> {
    Optional<Swipe> findBySwiperIdAndTargetId(Long swiperId, Long targetId);
}