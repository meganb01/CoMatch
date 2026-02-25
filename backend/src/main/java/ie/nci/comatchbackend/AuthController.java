package ie.nci.comatchbackend;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController: REST endpoints for authentication.
 * Base path: /api/auth
 * - POST /register – create new user (email, password)
 * - POST /login – validate credentials, return token
 * - POST /logout – invalidate token (requires Authorisation header)
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {

        this.authService = authService;
    }

    /**
     * Register: creates a new user account.
     * Client sends POST with JSON body { "email": "...", "password": "..." }.
     * @Valid = run validation on request (email format, password min 8 chars).
     * @RequestBody = map the JSON body to RegisterRequest object.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        // 201 CREATED = new user  was created successfully
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login: checks email and password, returns user info if valid.
     * Client sends POST with JSON body { "email": "...", "password": "..." }.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        // 200 OK = request succeeded
        return ResponseEntity.ok(response);
    }

    /**
     * Logout: client sends the auth token (Authorisation: Bearer <token>).
     * We invalidate the token in SessionStore so it can no longer be used.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        if (token == null) {
            throw new IllegalArgumentException("Missing Authorization header");
        }
        SessionStore.invalidate(token);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extracts the token from "Authorisation: Bearer <token>" header.
     * Returns null if header is missing or blank.
     */
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
