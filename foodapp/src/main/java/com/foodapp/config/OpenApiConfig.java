package com.foodapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration.
 *
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * API docs JSON at:     http://localhost:8080/v3/api-docs
 *
 * The "Authorize" button in Swagger UI lets you paste a JWT token
 * to test protected endpoints directly from the browser.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🍔 FoodApp — Multi-Vendor Food Ordering API")
                        .version("1.0.0")
                        .description("""
                            Production-ready REST API for a multi-vendor food ordering platform.
                            
                            **Features:** JWT auth, multi-vendor restaurants, real-time order tracking,
                            delivery assignment, payment integration, and admin analytics.
                            
                            **Test Login:** POST /api/auth/login with `{"email":"rahul@gmail.com","password":"password"}`
                            Then click "Authorize" and paste the accessToken.
                            """)
                        .contact(new Contact()
                                .name("FoodApp Team")
                                .email("support@foodapp.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Auth"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Auth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste your JWT access token here")));
    }
}
