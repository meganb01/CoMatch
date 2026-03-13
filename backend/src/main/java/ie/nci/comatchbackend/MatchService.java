package ie.nci.comatchbackend;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * MatchService: profile browsing (FR-010), filtering (FR-014/015/016), listing matches (FR-013).
 * Skips already-swiped users (like the DB stored procedure GetNextProfile).
 */
@Service
public class MatchService {

    private final UserProfileRepository userProfileRepository;
    private final ProfileService profileService;
    private final UserMatchRepository userMatchRepository;

    public MatchService(UserProfileRepository userProfileRepository,
                        ProfileService profileService,
                        UserMatchRepository userMatchRepository) {
        this.userProfileRepository = userProfileRepository;
        this.profileService = profileService;
        this.userMatchRepository = userMatchRepository;
    }

    /**
     * Find the next candidate profile. Skips current user and already-swiped users.
     * Supports optional filters: country (maps to location), industry (maps to sector_name), skill.
     */
    public Optional<FounderProfile> findNextCandidate(Long currentUserId, Long afterUserId,
                                                       String country, String industry, String skill) {
        Long startAfter = afterUserId != null ? afterUserId : currentUserId;

        List<UserProfile> results = userProfileRepository.findFilteredCandidatesAfter(
                currentUserId, startAfter, country, industry, skill, PageRequest.of(0, 1));
        Optional<UserProfile> next = results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));

        if (next.isEmpty()) {
            results = userProfileRepository.findFilteredCandidates(
                    currentUserId, country, industry, skill, PageRequest.of(0, 1));
            next = results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        }

        if (next.isEmpty()) {
            return Optional.empty();
        }

        Long candidateUserId = next.get().getUserId();
        FounderProfile profile = profileService.getProfile(candidateUserId);
        return Optional.of(profile);
    }

    @Transactional(readOnly = true)
    public List<FounderProfile> listMatches(Long userId) {
        List<UserMatch> matches = userMatchRepository.findAllByUserId(userId);
        return matches.stream()
                .map(m -> {
                    Long otherUserId = m.getUser1Id().equals(userId) ? m.getUser2Id() : m.getUser1Id();
                    return profileService.getProfile(otherUserId);
                })
                .toList();
    }
}
