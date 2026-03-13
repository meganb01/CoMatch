package ie.nci.comatchbackend;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DiscoverController: endpoints called by the discover.js frontend page.
 * GET  /api/profiles/discover  – all unswiped profiles (array)
 * POST /api/profiles/swipe     – record like/pass, returns { match: boolean }
 */
@RestController
@RequestMapping("/api/profiles")
@CrossOrigin
public class DiscoverController {

    private final UserProfileRepository userProfileRepository;
    private final SkillRepository skillRepository;
    private final SectorRepository sectorRepository;
    private final SwipeService swipeService;

    public DiscoverController(UserProfileRepository userProfileRepository,
                              SkillRepository skillRepository,
                              SectorRepository sectorRepository,
                              SwipeService swipeService) {
        this.userProfileRepository = userProfileRepository;
        this.skillRepository = skillRepository;
        this.sectorRepository = sectorRepository;
        this.swipeService = swipeService;
    }

    @GetMapping("/discover")
    public ResponseEntity<List<DiscoverProfile>> discover(
            @RequestHeader(name = "Authorization", required = false) String authorization) {

        Long currentUserId = requireAuthenticatedUser(authorization);

        List<UserProfile> candidates = userProfileRepository.findAllDiscoverable(currentUserId);

        List<DiscoverProfile> result = candidates.stream().map(up -> {
            List<String> skills = skillRepository.findSkillNamesByUserId(up.getUserId());
            List<String> sectors = sectorRepository.findSectorNamesByUserId(up.getUserId());
            String sector = sectors.isEmpty() ? "" : sectors.get(0);

            return new DiscoverProfile(
                    up.getUserId(),
                    up.getFullName(),
                    up.getPhotoUrl(),
                    sector,
                    skills,
                    up.getBio(),
                    up.getLocation()
            );
        }).toList();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/swipe")
    public ResponseEntity<Map<String, Object>> swipe(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody DiscoverSwipeRequest request) {

        Long currentUserId = requireAuthenticatedUser(authorization);

        String swipeType = request.getAction().equalsIgnoreCase("like") ? "LIKE" : "PASS";
        SwipeResponse sr = swipeService.recordSwipe(currentUserId, request.getTargetUserId(), swipeType);

        Map<String, Object> body = new HashMap<>();
        body.put("match", sr.isMatched());
        return ResponseEntity.ok(body);
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
