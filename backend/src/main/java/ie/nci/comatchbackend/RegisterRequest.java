package ie.nci.comatchbackend;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for the register request.
 * Client sends JSON: { "email": "...", "password": "..." }.
 * Validation runs when the controller uses @Valid on the parameter.
 */
public class RegisterRequest {

    /** Must be non-blank and valid email format. */
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    /** Must be non-blank and at least 8 characters (NFR: secure passwords). */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
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
