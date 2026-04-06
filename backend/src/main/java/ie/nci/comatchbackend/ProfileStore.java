package ie.nci.comatchbackend;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Legacy in-memory storage for founder profiles (keyed by userId).
 * Not used: profile data is now persisted via UserProfileRepository + JPA.
 */
public class ProfileStore {

    private static final Map<Long, FounderProfile> profiles = new ConcurrentHashMap<>();

    public static FounderProfile saveOrUpdate(FounderProfile profile) {
        profiles.put(profile.getUserId(), profile);
        return profile;
    }

    public static Optional<FounderProfile> findByUserId(Long userId) {
        return Optional.ofNullable(profiles.get(userId));
    }
}

