package ie.nci.comatchbackend;

/**
 * DTO for the response after successful register or login.
 * Spring converts this to JSON, e.g. { "userId": 1, "email": "user@example.com" }.
 * The frontend can save userId to know who is logged in.
 */
public class AuthResponse {

    private Long userId;
    private String email;

    public AuthResponse(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public Long getUserId() {

        return userId;
    }

    public String getEmail() {

        return email;
    }
}
