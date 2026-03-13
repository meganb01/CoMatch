package ie.nci.comatchbackend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * MatchController: REST endpoints for profile browsing, filtering, and match listing.
 * Base path: /api/matches
 *
 * - GET /api/matches/next   – browse one profile at a time (FR-010), with optional filters (FR-014/015/016)
 * - GET /api/matches         – list all mutual matches for the logged-in user (FR-013)
 *
 * All endpoints require Authorization: Bearer token (NFR-004).
 */
@RestController
@RequestMapping("/api/matches")
@CrossOrigin
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/next")
    public ResponseEntity<FounderProfile> getNextMatch(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestParam(name = "afterUserId", required = false) Long afterUserId,
            @RequestParam(name = "country", required = false) String country,
            @RequestParam(name = "industry", required = false) String industry,
            @RequestParam(name = "skill", required = false) String skill) {

        Long currentUserId = requireAuthenticatedUser(authorization);
        Optional<FounderProfile> next = matchService.findNextCandidate(
                currentUserId, afterUserId, country, industry, skill);
        return next.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping
    public ResponseEntity<List<FounderProfile>> getMyMatches(
            @RequestHeader(name = "Authorization", required = false) String authorization) {

        Long currentUserId = requireAuthenticatedUser(authorization);
        List<FounderProfile> matches = matchService.listMatches(currentUserId);
        return ResponseEntity.ok(matches);
    }

    private Long requireAuthenticatedUser(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        return SessionStore.getUserIdForToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or missing auth token"));
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }
        if (authorizationHeader.toLowerCase().startsWith("bearer ")) {
            return authorizationHeader.substring(7).trim();
        }
        return authorizationHeader.trim();
    }
}
