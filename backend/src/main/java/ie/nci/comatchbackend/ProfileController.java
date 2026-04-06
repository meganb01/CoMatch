package ie.nci.comatchbackend;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ProfileController: REST endpoints for founder profile.
 * Base path: /api/profile
 * All endpoints require Authorisation: Bearer <token> (from login).
 * - POST / – create or update current user's profile (name, bio, photo, skills, industry, country)
 * - GET /me – get current user's profile
 */
@RestController
@RequestMapping("/api/profile")
@CrossOrigin
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /** Create or update the logged-in user's profile. Request body: ProfileRequest (JSON). */
    @PostMapping
    public ResponseEntity<FounderProfile> createOrUpdateProfile(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Valid @RequestBody ProfileRequest request) {

        Long userId = requireAuthenticatedUser(authorization);

        FounderProfile saved = profileService.createOrUpdateProfile(userId, request);
        return ResponseEntity.ok(saved);
    }

    /** Get the logged-in user's own profile. Returns 400 if token missing/invalid. */
    @GetMapping("/me")
    public ResponseEntity<FounderProfile> getMyProfile(
            @RequestHeader(name = "Authorization", required = false) String authorization) {

        Long userId = requireAuthenticatedUser(authorization);

        FounderProfile profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Resolves Authorisation header to a valid userId.
     * Throws if token is missing or invalid (GlobalExceptionHandler returns 400).
     */
    private Long requireAuthenticatedUser(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        return SessionStore.getUserIdForToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or missing auth token"));
    }

    /** Extracts token from "Authorisation: Bearer <token>". */
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

