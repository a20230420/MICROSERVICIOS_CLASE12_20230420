package com.example.clase12.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CalculatorServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CalculatorServiceApplication.class, args);
    }
}
