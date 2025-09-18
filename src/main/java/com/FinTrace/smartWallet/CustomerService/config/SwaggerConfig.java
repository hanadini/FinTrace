package com.FinTrace.smartWallet.CustomerService.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // Swagger configuration can be added here if needed
    @Bean
    public OpenAPI SwalletOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("CRM API")
                        .description("API documentation for SWallet Customer application")
                        .version("v1.0"));
    }
}
