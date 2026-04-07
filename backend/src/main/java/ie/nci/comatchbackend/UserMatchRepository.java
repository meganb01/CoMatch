package ie.nci.comatchbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {

    boolean existsByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    @Query("SELECT m FROM UserMatch m WHERE m.user1Id = :uid OR m.user2Id = :uid")
    List<UserMatch> findAllByUserId(@Param("uid") Long userId);
}
