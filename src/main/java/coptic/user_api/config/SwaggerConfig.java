package coptic.user_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Configuration class for setting Swagger/OpenAPI metadata
@Configuration
public class SwaggerConfig {

    //Defines custom API metadata to display in Swagger UI
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coptic Scribe User API")
                        .version("1.0")
                        .description("Handles authentication and bookmark management for Coptic Scribe."));
    }
}
