package ie.nci.comatchbackend;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService:  logic for registration and login.
 * - Register: checks duplicate email, hashes password (BCrypt), saves user via UserRepository.
 * - Login: finds user by email, verifies password against hash, creates session token via SessionStore.
 * Exceptions (e.g. "Email already exists", "Invalid email or password") are turned into 400 JSON by GlobalExceptionHandler.
 */
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    /**
     * Register: check duplicate email, hash password, save user, return response.
     * Throws IllegalArgumentException if email already exists (GlobalExceptionHandler turns it into 400 JSON).
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        // Never store plain password - always hash (BCrypt) before saving
        String hash = passwordEncoder.encode(request.getPassword());
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(hash);
        User saved = userRepository.save(user);
        return new AuthResponse(saved.getUserId(), saved.getEmail());
    }

    /**
     * Login: find user by email, check password matches hash, return response.
     * Throws IllegalArgumentException if user not found or password wrong.
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        // Compare typed password with stored hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        String token = SessionStore.createSession(user.getUserId());
        return new AuthResponse(user.getUserId(), user.getEmail(), token);
    }
}
