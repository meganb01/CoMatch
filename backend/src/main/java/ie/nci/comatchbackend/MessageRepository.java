package ie.nci.comatchbackend;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByMatch_IdOrderByCreatedAtAsc(Long matchId, Pageable pageable);
}
