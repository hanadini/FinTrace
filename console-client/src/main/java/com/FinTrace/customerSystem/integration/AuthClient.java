package com.FinTrace.customerSystem.integration;

import com.FinTrace.customerSystem.config.CustomerClientConfig;
import com.FinTrace.customerSystem.dto.JwtResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "authClient",
        url = "http://localhost:8082/api/auth",
        contextId = "authClient"
)
public interface AuthClient {

    @PostMapping("/login")
    JwtResponse login(@RequestParam String username,
                      @RequestParam String password);

}
