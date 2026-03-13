package ie.nci.comatchbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for UserMatch entity.
 * Queries both sides of a match since either user can be user1 or user2.
 */
public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {

    @Query("SELECT m FROM UserMatch m WHERE m.user1Id = :userId OR m.user2Id = :userId ORDER BY m.matchedAt DESC")
    List<UserMatch> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM UserMatch m " +
           "WHERE (m.user1Id = :u1 AND m.user2Id = :u2) OR (m.user1Id = :u2 AND m.user2Id = :u1)")
    boolean existsBetweenUsers(@Param("u1") Long u1, @Param("u2") Long u2);
}
