package com.hospital.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration
 * Provides API documentation through Swagger UI
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Hospital Management System API",
                description = "A secure, scalable Hospital Management System backend using Spring Boot. " +
                        "This API manages patient records, doctor schedules, and appointments for a hospital or clinic.",
                version = "1.0.0",
                contact = @Contact(
                        name = "Hospital Management Team",
                        email = "support@hospital.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080/api",
                        description = "Development Server"
                ),
                @Server(
                        url = "https://api.hospital.com",
                        description = "Production Server"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT Authentication Token",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
}
