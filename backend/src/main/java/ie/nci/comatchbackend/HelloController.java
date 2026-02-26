package ie.nci.comatchbackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple health-check / test controller.
 * GET http://localhost:8080/api/hello – returns a welcome message.
 */
@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from CoMatch Backend!";
    }
}
