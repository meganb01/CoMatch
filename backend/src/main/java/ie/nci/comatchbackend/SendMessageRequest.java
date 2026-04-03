package ie.nci.comatchbackend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for {@code POST /api/matches/{matchId}/messages}.
 */
public class SendMessageRequest {

    @NotBlank(message = "Message body is required")
    @Size(max = 4000, message = "Message must be at most 4000 characters")
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
