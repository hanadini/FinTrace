package com.FinTrace.customerSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.FinTrace.customerSystem.integration")
public class SpringAppRunner {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringAppRunner.class);
        app.run(args);
    }
}
