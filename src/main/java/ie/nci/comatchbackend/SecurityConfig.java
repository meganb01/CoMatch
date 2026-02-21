package ie.nci.comatchbackend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig: we use Spring Security only for password hashing and to allow all API calls.
 * - We do NOT require login to call /api/auth/register or /api/auth/login (so frontend can call them).
 * - We provide BCryptPasswordEncoder to hash passwords before storing.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Permit all requests: no login required to access any URL.
     * CSRF disabled so that POST from frontend (e.g. another port) works without extra tokens.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /**
     * PasswordEncoder used to hash passwords (register) and check them (login).
     * BCrypt is a strong, industry-standard hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}