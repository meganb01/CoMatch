package ie.nci.comatchbackend;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * SwipeController: REST endpoint for swiping on profiles (FR-011).
 * POST /api/swipes – record a LIKE or PASS on another user.
 * Returns whether a mutual match was created (FR-012).
 * Requires Authorization: Bearer token.
 */
@RestController
@RequestMapping("/api/swipes")
@CrossOrigin
public class SwipeController {

    private final SwipeService swipeService;

    public SwipeController(SwipeService swipeService) {
        this.swipeService = swipeService;
    }

    @PostMapping
    public ResponseEntity<SwipeResponse> swipe(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody SwipeRequest request) {

        Long currentUserId = requireAuthenticatedUser(authorization);
        SwipeResponse response = swipeService.recordSwipe(
                currentUserId, request.getTargetUserId(), request.getSwipeType());
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
