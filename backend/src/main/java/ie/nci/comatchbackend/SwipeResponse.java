package ie.nci.comatchbackend;

/**
 * DTO returned after a swipe action.
 * recorded = true means the swipe was saved.
 * matched = true means a mutual match was created (both users liked each other).
 * matchId = the ID of the new match row (null if no match).
 */
public class SwipeResponse {

    private boolean recorded;
    private boolean matched;
    private Long matchId;

    public SwipeResponse(boolean recorded, boolean matched, Long matchId) {
        this.recorded = recorded;
        this.matched = matched;
        this.matchId = matchId;
    }

    public boolean isRecorded() { return recorded; }
    public boolean isMatched() { return matched; }
    public Long getMatchId() { return matchId; }
}
