package com.FinTrace.smartWallet.CustomerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class SmartWalletApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SmartWalletApplication.class);
        if(checkProfile("console")){
            app.setWebApplicationType(WebApplicationType.NONE);
        }
        app.run(args);
    }

    private static boolean checkProfile(String profile) {
        try {
            Resource resource = new ClassPathResource("application.properties");
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            String activeProfile = props.getProperty("spring.profiles.active");
            return activeProfile != null && activeProfile.contains(profile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
