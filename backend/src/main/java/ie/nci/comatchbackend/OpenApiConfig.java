package ie.nci.comatchbackend;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 / Swagger UI: {@code /swagger-ui.html}, JSON docs at {@code /v3/api-docs}.
 * Authorize with {@code Bearer <token>} from {@code POST /api/auth/login}.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER = "bearerAuth";

    @Bean
    public OpenAPI comatchOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CoMatch API")
                        .version("0.1.0")
                        .description("REST API for CoMatch. Obtain a session token via POST /api/auth/login, "
                                + "then use Authorization: Bearer <token> on protected routes."))
                .components(new Components()
                        .addSecuritySchemes(BEARER,
                                new SecurityScheme()
                                        .name(BEARER)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .description("Opaque session token returned by login (not JWT).")));
    }
}
