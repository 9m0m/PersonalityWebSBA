package com.sba301.persona_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI personaServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Persona Service API")
                        .description("Persona Service APIs for SBA301")
                        .version("1.0"));
    }
}
