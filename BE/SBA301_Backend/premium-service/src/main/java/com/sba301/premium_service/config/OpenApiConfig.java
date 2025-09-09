package com.sba301.premium_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI personaServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Premium Service API")
                        .description("Premium Service APIs for SBA301")
                        .version("1.0"))
                .components(new Components().addSecuritySchemes(
                        "BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                ))
                .security(List.of(new SecurityRequirement().addList("BearerAuth")));
    }
}
