package ie.nci.comatchbackend;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * FR-017: POST /api/matches/{matchId}/messages — send a message.
 * FR-018 / FR-019: GET /api/matches/{matchId}/messages — paginated chat history (oldest first within page).
 */
@RestController
@RequestMapping("/api/matches/{matchId}/messages")
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> send(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long matchId,
            @Valid @RequestBody SendMessageRequest request) {

        Long userId = requireAuthenticatedUser(authorization);
        MessageResponse created = messageService.sendMessage(matchId, userId, request.getBody());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<Page<MessageResponse>> list(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable Long matchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Long userId = requireAuthenticatedUser(authorization);
        Page<MessageResponse> result = messageService.listMessages(matchId, userId, page, size);
        return ResponseEntity.ok(result);
    }

    private Long requireAuthenticatedUser(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        return SessionStore.getUserIdForToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or missing auth token"));
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }
        if (authorizationHeader.toLowerCase().startsWith("bearer ")) {
            return authorizationHeader.substring(7).trim();
        }
        return authorizationHeader.trim();
    }
}
