package com.FinTrace.customerSystem.integration;

import com.FinTrace.customerSystem.dto.JwtResponse;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientAuthInterceptor implements RequestInterceptor {

    private final AuthClient authClient;

    private volatile String jwtToken;

    @Autowired
    public ClientAuthInterceptor(AuthClient authClient) {
        this.authClient = authClient;
    }

    private synchronized void authenticate() {
        if (jwtToken == null) {
            JwtResponse login = authClient.login("admin", "admin123");
            this.jwtToken = login.getToken();
        }
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if(jwtToken == null) {
            authenticate();
        }
        requestTemplate.header("Authorization", "Bearer " + jwtToken);
    }
}