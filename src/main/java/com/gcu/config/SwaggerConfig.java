package com.gcu.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.plaf.basic.BasicToolBarUI;


/**
 * Configuration class for Swagger, which provides automated documentation
 * and testing interface for REST APIs in com.gcu.controller.REST.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info().title("Blodged Web App").description("This is the blodged spring boot web app.").version("1.0.0"));

    }

}
