package ie.nci.comatchbackend;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin
public class MatchController {

    private final MatchRepository matchRepository;

    public MatchController(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @GetMapping
    public ResponseEntity<?> getMatches(
            @RequestHeader(name = "Authorization", required = false) String authorization) {

        Long userId = requireAuth(authorization);

        List<Map<String, Object>> result = matchRepository.findByUserId(userId)
                .stream()
                .map(m -> {
                    Long otherId = m.getUser1Id().equals(userId) ? m.getUser2Id() : m.getUser1Id();
                    return Map.<String, Object>of(
                            "matchId", m.getId(),
                            "otherUserId", otherId
                    );
                })
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