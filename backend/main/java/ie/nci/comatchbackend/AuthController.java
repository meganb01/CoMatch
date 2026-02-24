package ie.nci.comatchbackend;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController handles HTTP requests for registration and login.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    // Spring injects AuthService here (dependency injection)
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
        // 201 CREATED = new resource (user) was created successfully
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
}
