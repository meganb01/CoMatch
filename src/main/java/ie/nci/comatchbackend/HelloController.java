package ie.nci.comatchbackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple test controller: proves the server is running and can handle GET requests.
 * Open http://localhost:8080/api/hello in browser to see the message.
 */
@RestController
public class HelloController {

    /** GET /api/hello returns this text (no request body, no parameters). */
    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from CoMatch Backend!";
    }
}
