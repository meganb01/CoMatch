package ie.nci.comatchbackend;

/**
 * DTO for swipe request body.
 * Client sends: { "targetUserId": 123, "action": "like" | "pass" }
 */
public class SwipeRequest {

    private Long targetUserId;
    private String action;

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
