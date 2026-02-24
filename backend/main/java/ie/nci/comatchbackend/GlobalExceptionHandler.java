package ie.nci.comatchbackend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler: when any controller throws an exception, Spring can send it here.
 * We convert exceptions into JSON responses with a proper HTTP status (e.g. 400 Bad Request)
 * so the frontend gets a consistent error format instead of a stack trace.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * When AuthService throws IllegalArgumentException (e.g. "Email already exists",
     * "Invalid email or password"), we return 400 with body { "error": "message" }.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * When validation fails on @Valid (e.g. email format wrong, password too short),
     * Spring throws MethodArgumentNotValidException. We return 400 with field names
     * and messages, e.g. { "email": "must be a valid email", "password": "size must be >= 8" }.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> body = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                body.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
