package com.example.clase12.calculator;

import feign.Request;
import feign.RetryableException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CalculatorControllerTest {
    @Test void delegaLaOperacionAlClienteRemoto() {
        AdditionClient client = mock(AdditionClient.class);
        when(client.addition(1,5)).thenReturn("(1 + 5) = 6");
        var result = new CalculatorController(client).calculate(1,5);
        assertEquals(200, result.getStatusCode().value());
        assertEquals("Calculator : (1 + 5) = 6", result.getBody());
        verify(client).addition(1,5);
    }

    @Test void devuelve503SiElServicioRemotoFalla() {
        AdditionClient client = mock(AdditionClient.class);
        Request request = Request.create(Request.HttpMethod.GET, "http://addition-service/addition/1/5",
                Map.of(), null, java.nio.charset.StandardCharsets.UTF_8, null);
        when(client.addition(1,5)).thenThrow(new RetryableException(-1, "Sin conexion",
                Request.HttpMethod.GET, (Long) null, request));
        var result = new CalculatorController(client).calculate(1,5);
        assertEquals(503, result.getStatusCode().value());
    }
}
