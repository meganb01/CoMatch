package ie.nci.comatchbackend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler: converts controller exceptions into JSON error responses.
 * Ensures the frontend always receives a consistent format (e.g. { "error": "..." } or field errors)
 * instead of HTML error pages or stack traces.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Business rule violations (e.g. "Email already exists", "Invalid email or password",
     * "Invalid or missing auth token"). Returns 400 with body { "error": "message" }.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Validation failures on @Valid request bodies (e.g. invalid email, password too short).
     * Returns 400 with a map of field name to message, e.g. { "email": "...", "password": "..." }.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> body = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                body.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
