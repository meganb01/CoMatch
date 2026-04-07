package ie.nci.comatchbackend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lists mutual matches for the logged-in user.
 */
@Service
public class MatchService {

    private final UserMatchRepository userMatchRepository;
    private final ProfileService profileService;

    public MatchService(UserMatchRepository userMatchRepository, ProfileService profileService) {
        this.userMatchRepository = userMatchRepository;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public List<FounderProfile> listMatches(Long userId) {
        return userMatchRepository.findAllByUserId(userId).stream()
                .map(m -> {
                    Long otherUserId = m.getUser1Id().equals(userId) ? m.getUser2Id() : m.getUser1Id();
                    return profileService.getProfile(otherUserId);
                })
                .toList();
    }
}
