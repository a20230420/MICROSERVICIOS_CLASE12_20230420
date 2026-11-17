package com.example.clase12.addition;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdditionController {
    @GetMapping("/addition/{item_a}/{item_b}")
    public String addition(@PathVariable("item_a") int a, @PathVariable("item_b") int b) {
        // Convertir antes de sumar evita el desbordamiento de int.
        long sum = (long) a + b;
        return "(" + a + " + " + b + ") = " + sum;
    }
}
