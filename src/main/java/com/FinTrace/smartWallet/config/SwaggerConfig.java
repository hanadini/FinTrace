package com.FinTrace.smartWallet.config;

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
                        .title("Smart Wallet API")
                        .description("API documentation for Smart Wallet application")
                        .version("v1.0"));
    }
}
