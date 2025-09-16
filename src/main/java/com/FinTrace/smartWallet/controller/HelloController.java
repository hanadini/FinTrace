package com.FinTrace.smartWallet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @GetMapping("/world")
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/name/{name}")
    public String helloByName(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping()
    public String helloempty() {
        return "Hello!" ;
    }
}
