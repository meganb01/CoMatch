package ie.nci.comatchbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the CoMatch backend.
 * Starts the Spring Boot application; REST API runs on port 8080S.
 */
@SpringBootApplication
public class ComatchBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComatchBackendApplication.class, args);
    }
}

