package ie.nci.comatchbackend;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Legacy in-memory store for users (no database).
 * Not used: auth now uses UserRepository + JPA (H2/MySQL). Kept for reference.
 */
public class MockUserStore {

    // List of all registered users (lives only in memory)
    private static final List<StoredUser> users = new ArrayList<>();
    // Generates unique IDs: 1, 2, 3, ... (thread-safe)
    private static final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Represents one user in our store: id, email, and hashed password (never plain text).
     */
    public static class StoredUser {
        private final Long id;
        private final String email;
        private final String passwordHash;

        public StoredUser(Long id, String email, String passwordHash) {
            this.id = id;
            this.email = email;
            this.passwordHash = passwordHash;
        }

        public Long getId() { return id; }
        public String getEmail() { return email; }
        public String getPasswordHash() { return passwordHash; }
    }

    /** Find a user by email (case-insensitive). Returns empty if not found. */
    public static Optional<StoredUser> findByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    /** Check if a user with this email already exists (for duplicate check on register). */
    public static boolean existsByEmail(String email) {

        return findByEmail(email).isPresent();
    }

    /** Save a new user (email + already-hashed password). Returns the saved user with generated id. */
    public static StoredUser save(String email, String passwordHash) {
        Long id = idGenerator.getAndIncrement();
        StoredUser u = new StoredUser(id, email, passwordHash);
        users.add(u);
        return u;
    }
}
