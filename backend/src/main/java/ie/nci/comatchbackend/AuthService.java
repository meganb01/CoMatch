package ie.nci.comatchbackend;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
    private final UserProfileRepository userProfileRepository;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository,
                       UserProfileRepository userProfileRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * Register: check duplicate email, hash password, save user, create profile with name from email, return response.
     * Name is derived from email (part before @), e.g. test@test.in -> "test".
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        String hash = passwordEncoder.encode(request.getPassword());
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(hash);
        User saved = userRepository.save(user);

        // Auto-create profile with name from email (part before @)
        String nameFromEmail = extractNameFromEmail(request.getEmail());
        UserProfile profile = new UserProfile();
        profile.setUserId(saved.getUserId());
        profile.setFullName(nameFromEmail);
        userProfileRepository.save(profile);

        return new AuthResponse(saved.getUserId(), saved.getEmail());
    }

    /** Extract display name from email: test@test.in -> test */
    private String extractNameFromEmail(String email) {
        if (email == null || email.isBlank()) return "";
        int at = email.indexOf('@');
        return at > 0 ? email.substring(0, at).trim() : email.trim();
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
