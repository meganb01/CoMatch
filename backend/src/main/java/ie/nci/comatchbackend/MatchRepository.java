package ie.nci.comatchbackend;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("SELECT m FROM Match m WHERE m.user1Id = :uid OR m.user2Id = :uid")
    List<Match> findByUserId(@Param("uid") Long userId);

    @Query("SELECT m FROM Match m WHERE (m.user1Id = :a AND m.user2Id = :b) OR (m.user1Id = :b AND m.user2Id = :a)")
    Optional<Match> findByBothUsers(@Param("a") Long a, @Param("b") Long b);
}