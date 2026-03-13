package ie.nci.comatchbackend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for POST /api/profiles/swipe.
 * Matches the JSON that discover.js sends: { targetUserId, action }.
 */
public class DiscoverSwipeRequest {

    @NotNull(message = "targetUserId is required")
    private Long targetUserId;

    @NotBlank(message = "action is required (like or pass)")
    private String action;

    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
