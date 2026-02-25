package ie.nci.comatchbackend;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for the login request.
 * Client sends JSON: { "email": "...", "password": "..." }.
 * Validation runs when the controller uses @Valid on the parameter.
 */
public class LoginRequest {

    /** Must be non-blank and valid email format. */
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    /** Must be non-blank (exact match checked in AuthService against stored hash). */
    @NotBlank(message = "Password is required")
    private String password;

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }
}
