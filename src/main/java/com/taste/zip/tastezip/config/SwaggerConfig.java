package com.taste.zip.tastezip.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String key = "JWT 토큰만 입력하시오";

        return new OpenAPI()
            .info(new Info()
                .title("Taste-zip API")
                .version("1.0.0"))
            .components(new Components()
                .addSecuritySchemes(key,
                    new io.swagger.v3.oas.models.security.SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            )
            .addSecurityItem(new SecurityRequirement()
                .addList(key)
            );
    }
}
