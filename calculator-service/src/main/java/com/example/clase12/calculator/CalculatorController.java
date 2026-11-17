package com.example.clase12.calculator;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculatorController {
    private final AdditionClient additionClient;

    public CalculatorController(AdditionClient additionClient) {
        this.additionClient = additionClient;
    }

    @GetMapping("/addcalculator/{item_a}/{item_b}")
    public ResponseEntity<String> calculate(@PathVariable("item_a") int a,
                                          @PathVariable("item_b") int b) {
        try {
            // Esta llamada HTTP ejecuta la suma en OTRO proceso: Addition.
            return ResponseEntity.ok("Calculator : " + additionClient.addition(a, b));
        } catch (FeignException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Addition no esta disponible. Revisa que este iniciado y registrado en Eureka.");
        }
    }
}
