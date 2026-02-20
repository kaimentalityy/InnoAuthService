package com.innowise.config;

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
 * Configuration class for OpenAPI/Swagger documentation.
 * <p>
 * Configures API information, security schemes (JWT Bearer authentication),
 * and global security requirements for the Auth Service API.
 * </p>
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI specification for the Auth Service.
     * <p>
     * Sets up API metadata including title, description, version, contact
     * information,
     * and license. Also configures JWT Bearer token authentication scheme.
     * </p>
     *
     * @return configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI authServiceOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Inno Auth Service API")
                        .description("Authentication and Authorization Service API for managing user authentication, " +
                                "JWT token generation, validation, and refresh operations.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Innowise Team")
                                .email("support@innowise.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT authentication token. " +
                                                "Obtain this token by calling the /api/auth/login or /api/auth/register endpoints.")));
    }
}
