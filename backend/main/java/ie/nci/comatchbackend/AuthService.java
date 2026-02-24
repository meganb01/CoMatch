package ie.nci.comatchbackend;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService contains the business logic for register and login.
 * Controller calls this class; this class uses MockUserStore and PasswordEncoder.
 */
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    public AuthService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register: check duplicate email, hash password, save user, return response.
     * Throws IllegalArgumentException if email already exists (GlobalExceptionHandler turns it into 400 JSON).
     */
    public AuthResponse register(RegisterRequest request) {
        if (MockUserStore.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        // Never store plain password - always hash (BCrypt) before saving
        String hash = passwordEncoder.encode(request.getPassword());
        MockUserStore.StoredUser user = MockUserStore.save(request.getEmail(), hash);
        return new AuthResponse(user.getId(), user.getEmail());
    }

    /**
     * Login: find user by email, check password matches hash, return response.
     * Throws IllegalArgumentException if user not found or password wrong.
     */
    public AuthResponse login(LoginRequest request) {
        MockUserStore.StoredUser user = MockUserStore.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        // Compare typed password with stored hash (we never store plain password)
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return new AuthResponse(user.getId(), user.getEmail());
    }
}
