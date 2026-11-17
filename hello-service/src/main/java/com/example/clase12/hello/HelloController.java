package com.example.clase12.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/greeting")
    public String greeting() {
        return "Hello from 'hello-service'!";
    }
}
