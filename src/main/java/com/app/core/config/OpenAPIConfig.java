package com.app.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    public static final String BEARER_SECURITY_SCHEME_NAME = "bearerAuth";

    private static final String BEARER_SCHEME = "bearer";
    private static final String BEARER_JWT_FORMAT = "JWT";
    private static final String SECURITY_SCHEME_DESC =
            "JWT Authorization header using the Bearer scheme. Example: 'Bearer {token}'";

    @Bean
    public OpenAPI customOpenAPI() {


        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(BEARER_SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .in(SecurityScheme.In.HEADER)
                                        .scheme(BEARER_SCHEME)
                                        .bearerFormat(BEARER_JWT_FORMAT)
                                        .description(SECURITY_SCHEME_DESC)))
                .info(new Info()
                        .title("Book API")
                        .description("REST API for Book Library")
                        .version("0.0.1"));
    }
}