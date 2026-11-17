package com.example.clase12.calculator;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// El nombre coincide con spring.application.name de Addition.
// Eureka descubre su direccion; no escribimos un puerto fijo aqui.
@FeignClient(name = "addition-service")
public interface AdditionClient {
    @GetMapping("/addition/{item_a}/{item_b}")
    String addition(@PathVariable("item_a") int a, @PathVariable("item_b") int b);
}
