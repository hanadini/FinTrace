package com.FinTrace.customerService.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SwaggerController {
    @GetMapping("/api-ui/main")
    public String apiUI(){
        return "forward:/swagger-ui/index.html";
    }

    @RequestMapping("/api-ui/**")
    public String proxySwagger(HttpServletRequest request){
        String path = request.getRequestURI().replaceFirst("/api-ui", "/swagger-ui");
        return "forward:"+path;
    }
}
