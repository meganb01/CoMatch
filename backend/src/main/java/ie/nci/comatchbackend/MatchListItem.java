package ie.nci.comatchbackend;

/**
 * One mutual match for the current user: {@code matchId} for messaging APIs, plus the other user's profile.
 */
public class MatchListItem {

    private Long matchId;
    private FounderProfile profile;

    public MatchListItem(Long matchId, FounderProfile profile) {
        this.matchId = matchId;
        this.profile = profile;
    }

    public Long getMatchId() {
        return matchId;
    }

    public FounderProfile getProfile() {
        return profile;
    }
}
