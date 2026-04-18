package ie.nci.comatchbackend;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_id", nullable = false)
    private Long matchId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(nullable = false, length = 2000)
    private String body;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Message() {}

    public Message(Long matchId, Long senderId, String body) {
        this.matchId = matchId;
        this.senderId = senderId;
        this.body = body;
    }

    public Long getId()          { return id; }
    public Long getMatchId()     { return matchId; }
    public Long getSenderId()    { return senderId; }
    public String getBody()      { return body; }
    public Instant getCreatedAt(){ return createdAt; }
}