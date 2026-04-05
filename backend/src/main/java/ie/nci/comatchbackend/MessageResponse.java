package ie.nci.comatchbackend;

import java.time.LocalDateTime;

/**
 * JSON for one chat message (FR-018 / FR-019).
 * {@code senderName} is the sender's profile display name when available (FR-018).
 */
public class MessageResponse {

    private Long id;
    private Long matchId;
    private Long senderUserId;
    private String senderName;
    private String body;
    private LocalDateTime createdAt;

    public MessageResponse(Long id, Long matchId, Long senderUserId, String senderName, String body, LocalDateTime createdAt) {
        this.id = id;
        this.matchId = matchId;
        this.senderUserId = senderUserId;
        this.senderName = senderName;
        this.body = body;
        this.createdAt = createdAt;
    }

    public static MessageResponse fromEntity(Message m, String senderName) {
        String name = senderName != null ? senderName : "";
        return new MessageResponse(
                m.getId(),
                m.getMatch().getId(),
                m.getSenderUserId(),
                name,
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

    public String getSenderName() {
        return senderName;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
