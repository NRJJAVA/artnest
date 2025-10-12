package com.artnest.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ArtNest API")
                        .version("1.0.0")
                        .description("REST APIs for ArtNest Application")
                        .contact(new Contact()
                                .name("Neeraj Kumar Kushwaha")
                                .email("neeraj@example.com")
                                .url("https://www.artnest.com"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("ArtNest Documentation")
                        .url("https://www.artnest.com/docs"));
    }
}

