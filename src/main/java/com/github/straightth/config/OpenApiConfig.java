package com.github.straightth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        var securityScheme = new SecurityScheme()
                .name("Bearer token")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
        var components = new Components().addSecuritySchemes(securityScheme.getName(), securityScheme);
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securityScheme.getName()))
                .components(components);
    }
}
