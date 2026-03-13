package ie.nci.comatchbackend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DiscoverController: REST endpoints for discover feed and swipe.
 * Base path: /api/profiles
 * All endpoints require Authorization: Bearer &lt;token&gt; (from login).
 * - GET /discover – list discoverable profiles (excluding current user)
 * - POST /swipe – record like or pass
 */
@RestController
@RequestMapping("/api/profiles")
@CrossOrigin
public class DiscoverController {

    private final DiscoverService discoverService;

    public DiscoverController(DiscoverService discoverService) {
        this.discoverService = discoverService;
    }

    /** Get discoverable profiles (other users who have a profile). */
    @GetMapping("/discover")
    public ResponseEntity<List<FounderProfile>> getDiscover(
            @RequestHeader(name = "Authorization", required = false) String authorization) {

        Long userId = requireAuthenticatedUser(authorization);
        List<FounderProfile> profiles = discoverService.getDiscoverableProfiles(userId);
        return ResponseEntity.ok(profiles);
    }

    /** Process a swipe (like or pass). */
    @PostMapping("/swipe")
    public ResponseEntity<DiscoverService.SwipeResponse> swipe(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestBody SwipeRequest request) {

        Long userId = requireAuthenticatedUser(authorization);
        Long targetUserId = request.getTargetUserId();
        if (targetUserId == null) {
            throw new IllegalArgumentException("targetUserId is required");
        }
        String action = request.getAction() != null ? request.getAction() : "pass";

        DiscoverService.SwipeResponse response = discoverService.swipe(userId, targetUserId, action);
        return ResponseEntity.ok(response);
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
