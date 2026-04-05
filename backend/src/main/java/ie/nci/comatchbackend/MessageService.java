package ie.nci.comatchbackend;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Messaging for mutual matches only (FR-017, FR-018, FR-019).
 */
@Service
public class MessageService {

    static final int MAX_PAGE_SIZE = 100;

    private final UserMatchRepository userMatchRepository;
    private final MessageRepository messageRepository;
    private final ProfileService profileService;

    public MessageService(UserMatchRepository userMatchRepository, MessageRepository messageRepository,
                          ProfileService profileService) {
        this.userMatchRepository = userMatchRepository;
        this.messageRepository = messageRepository;
        this.profileService = profileService;
    }

    @Transactional
    public MessageResponse sendMessage(Long matchId, Long senderUserId, String body) {
        UserMatch match = userMatchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));
        assertParticipant(senderUserId, match);
        Message m = new Message();
        m.setMatch(match);
        m.setSenderUserId(senderUserId);
        m.setBody(body.trim());
        messageRepository.save(m);
        return MessageResponse.fromEntity(m, resolveSenderName(senderUserId));
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> listMessages(Long matchId, Long readerUserId, int page, int size) {
        UserMatch match = userMatchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));
        assertParticipant(readerUserId, match);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("createdAt").ascending());
        return messageRepository.findByMatch_IdOrderByCreatedAtAsc(matchId, pageable)
                .map(m -> MessageResponse.fromEntity(m, resolveSenderName(m.getSenderUserId())));
    }

    private String resolveSenderName(Long senderUserId) {
        String name = profileService.getProfile(senderUserId).getName();
        return name != null && !name.isBlank() ? name : "";
    }

    private static void assertParticipant(Long userId, UserMatch match) {
        boolean ok = match.getUser1Id().equals(userId) || match.getUser2Id().equals(userId);
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this match");
        }
    }
}
