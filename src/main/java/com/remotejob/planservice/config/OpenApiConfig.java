package com.remotejob.planservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig is a configuration class for OpenAPI documentation.
 * It configures the OpenAPI specification for the Plan Service API,
 * including API information and JWT authentication scheme.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI specification for the Plan Service API.
     * This includes API metadata such as title, version, description,
     * and security schemes for JWT authentication.
     *
     * @return an {@code OpenAPI} object containing the API specification
     */
    @Bean
    public OpenAPI planServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Plan Service API")
                        .description("API for managing subscription plans and plan-related operations")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Remote Job Team")
                                .email("support@remotejob.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
