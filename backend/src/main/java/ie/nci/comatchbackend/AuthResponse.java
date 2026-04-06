package ie.nci.comatchbackend;

/**
 * DTO for the response after successful register or login.
 * Serialised to JSON, e.g. { "userId": 1, "email": "user@example.com", "token": "..." }.
 * Frontend stores the token and sends it as Authorisation: Bearer <token> for profile API calls.
 */
public class AuthResponse {

    private Long userId;
    private String email;
    /** Returned on login only; client must send it in Authorisation header for /api/profile. */
    private String token;

    /** Used for register response (no token). */
    public AuthResponse(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    /** Used for login response (includes token). */
    public AuthResponse(Long userId, String email, String token) {
        this.userId = userId;
        this.email = email;
        this.token = token;
    }

    public Long getUserId() {

        return userId;
    }

    public String getEmail() {

        return email;
    }

    public String getToken() {

        return token;
    }
}
