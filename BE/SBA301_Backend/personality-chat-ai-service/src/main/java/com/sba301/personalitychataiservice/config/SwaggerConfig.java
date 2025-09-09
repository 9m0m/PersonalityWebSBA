package com.sba301.personalitychataiservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Personality Chat AI API")
                        .version("1.0")
                        .description("API documentation for Personality-based Chat AI Service")
                        .license(new License().name("MIT License")));
    }
}
