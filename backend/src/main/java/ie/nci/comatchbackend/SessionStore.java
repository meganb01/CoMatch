package ie.nci.comatchbackend;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory session store: maps auth tokens (from login) to user IDs.
 * Used by ProfileController and AuthController (logout) to resolve Bearer token to userId.
 * All data is lost when the application restarts; tokens are not persisted.
 */
public class SessionStore {

    private static final Map<String, Long> tokenToUserId = new ConcurrentHashMap<>();

    /** Create a new session for the user; returns the token to send to the client. */
    public static String createSession(Long userId) {
        String token = UUID.randomUUID().toString();
        tokenToUserId.put(token, userId);
        return token;
    }

    /** Look up userId for a token; empty if token is null, blank, or unknown. */
    public static Optional<Long> getUserIdForToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(tokenToUserId.get(token));
    }

    /** Remove the token (e.g. on logout); it can no longer be used. */
    public static void invalidate(String token) {
        if (token != null) {
            tokenToUserId.remove(token);
        }
    }
}

