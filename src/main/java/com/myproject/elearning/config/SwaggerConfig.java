package com.myproject.elearning.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                //                .components(new Components()
                //                        .addSecuritySchemes(
                //                                securitySchemeName,
                //                                new SecurityScheme()
                //                                        .name(securitySchemeName)
                //                                        .type(SecurityScheme.Type.HTTP)
                //                                        .scheme("bearer")
                //                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("E-Learning API Documentation")
                        .description("API documentation for E-Learning platform")
                        .version("1.0"));
    }
}
