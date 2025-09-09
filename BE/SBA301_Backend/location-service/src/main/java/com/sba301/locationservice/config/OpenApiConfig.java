    package com.sba301.locationservice.config;

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
        public OpenAPI openAPI() {
            return new OpenAPI()
                    .info(new Info()
                            .title("Location Service API")
                            .description("Location Service APIs for SBA301")
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
