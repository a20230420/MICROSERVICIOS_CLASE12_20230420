package com.example.clase12.addition;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AdditionControllerTest {
    private final AdditionController controller = new AdditionController();

    @Test void sumaPositivos() { assertEquals("(1 + 5) = 6", controller.addition(1,5)); }
    @Test void aceptaNegativos() { assertEquals("(-8 + 3) = -5", controller.addition(-8,3)); }
    @Test void evitaDesbordamientoInt() {
        assertEquals("(2147483647 + 1) = 2147483648", controller.addition(Integer.MAX_VALUE,1));
    }
}
