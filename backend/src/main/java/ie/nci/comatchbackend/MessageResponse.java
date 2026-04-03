package ie.nci.comatchbackend;

import java.time.LocalDateTime;

/**
 * JSON for one chat message (FR-018 / FR-019).
 */
public class MessageResponse {

    private Long id;
    private Long matchId;
    private Long senderUserId;
    private String body;
    private LocalDateTime createdAt;

    public MessageResponse(Long id, Long matchId, Long senderUserId, String body, LocalDateTime createdAt) {
        this.id = id;
        this.matchId = matchId;
        this.senderUserId = senderUserId;
        this.body = body;
        this.createdAt = createdAt;
    }

    public static MessageResponse fromEntity(Message m) {
        return new MessageResponse(
                m.getId(),
                m.getMatch().getId(),
                m.getSenderUserId(),
                m.getBody(),
                m.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public Long getMatchId() {
        return matchId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
