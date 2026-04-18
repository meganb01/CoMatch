package ie.nci.comatchbackend;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin
public class MessageController {

    private final MessageRepository messageRepository;
    private final MatchRepository matchRepository;

    public MessageController(MessageRepository messageRepository,
                             MatchRepository matchRepository) {
        this.messageRepository = messageRepository;
        this.matchRepository = matchRepository;
    }

    @PostMapping("/{matchId}")
    public ResponseEntity<?> sendMessage(
            @PathVariable Long matchId,
            @RequestBody Map<String, String> body,
            @RequestHeader(name = "Authorization", required = false) String authorization) {

        Long userId = requireAuth(authorization);

        String text = body.get("body");
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body("Message body cannot be empty");
        }

        Match match = matchRepository.findById(matchId).orElse(null);
        if (match == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isParticipant = match.getUser1Id().equals(userId)
                || match.getUser2Id().equals(userId);
        if (!isParticipant) {
            return ResponseEntity.status(403).body("Not a participant of this match");
        }

        Message saved = messageRepository.save(new Message(matchId, userId, text));
        return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "matchId", saved.getMatchId(),
                "senderId", saved.getSenderId(),
                "body", saved.getBody(),
                "createdAt", saved.getCreatedAt().toString()
        ));
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<?> listMessages(
            @PathVariable Long matchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(name = "Authorization", required = false) String authorization) {

        Long userId = requireAuth(authorization);

        if (page < 0) page = 0;
        if (size < 1) size = 1;
        if (size > 100) size = 100;

        Match match = matchRepository.findById(matchId).orElse(null);
        if (match == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isParticipant = match.getUser1Id().equals(userId)
                || match.getUser2Id().equals(userId);
        if (!isParticipant) {
            return ResponseEntity.status(403).body("Not a participant of this match");
        }

        List<Message> messages = messageRepository
                .findByMatchIdOrderByCreatedAtAsc(matchId, PageRequest.of(page, size));

        List<Map<String, Object>> result = messages.stream()
                .map(m -> Map.<String, Object>of(
                        "id", m.getId(),
                        "senderId", m.getSenderId(),
                        "body", m.getBody(),
                        "createdAt", m.getCreatedAt().toString()
                ))
                .toList();

        return ResponseEntity.ok(result);
    }

    private Long requireAuth(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        return SessionStore.getUserIdForToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or missing auth token"));
    }

    private String extractToken(String header) {
        if (header == null || header.isBlank()) return null;
        return header.toLowerCase().startsWith("bearer ")
                ? header.substring(7).trim() : header.trim();
    }
}