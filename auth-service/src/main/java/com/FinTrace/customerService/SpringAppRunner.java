package com.FinTrace.customerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAppRunner {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringAppRunner.class);
        app.run(args);
    }
}
