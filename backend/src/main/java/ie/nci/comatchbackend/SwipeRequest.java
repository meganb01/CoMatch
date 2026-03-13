package ie.nci.comatchbackend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for swipe actions.
 * Client sends: { "targetUserId": 5, "swipeType": "LIKE" }
 */
public class SwipeRequest {

    @NotNull(message = "Target user ID is required")
    private Long targetUserId;

    @NotBlank(message = "Swipe type is required (LIKE or PASS)")
    private String swipeType;

    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }

    public String getSwipeType() { return swipeType; }
    public void setSwipeType(String swipeType) { this.swipeType = swipeType; }
}
