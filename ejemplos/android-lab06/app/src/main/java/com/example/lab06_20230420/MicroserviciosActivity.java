package com.example.lab06_20230420;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Pantalla opcional para copiar al Lab06; conserva su acceso con Firebase. */
public class MicroserviciosActivity extends PantallaPrivada {
    // Emulador Android -> PC anfitriona. En un celular real se cambia la direccion.
    private static final String BASE_URL = "http://10.0.2.2:8020";
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final CalculatorHttpClient client = new CalculatorHttpClient(BASE_URL);
    private EditText numberA, numberB;
    private TextView result;
    private Button calculate;

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        if (isFinishing() || repository == null) return;
        setContentView(R.layout.activity_microservicios);
        ajustarBarras();
        numberA = findViewById(R.id.mc_numero_a);
        numberB = findViewById(R.id.mc_numero_b);
        result = findViewById(R.id.mc_resultado);
        calculate = findViewById(R.id.mc_calcular);
        findViewById(R.id.mc_volver).setOnClickListener(v -> finish());
        calculate.setOnClickListener(v -> calculate());
    }

    private void calculate() {
        final int a, b;
        try {
            a = Integer.parseInt(numberA.getText().toString().trim());
            b = Integer.parseInt(numberB.getText().toString().trim());
        } catch (NumberFormatException ex) {
            result.setText(R.string.mc_enteros);
            return;
        }
        calculate.setEnabled(false);
        result.setText(R.string.mc_consultando);
        worker.execute(() -> {
            String message;
            try {
                message = client.sumar(a, b);
            } catch (IOException ex) {
                message = getString(R.string.mc_error, ex.getMessage());
            }
            final String response = message;
            runOnUiThread(() -> {
                if (isDestroyed() || isFinishing()) return;
                result.setText(response);
                calculate.setEnabled(true);
            });
        });
    }

    @Override protected void onDestroy() {
        worker.shutdownNow();
        super.onDestroy();
    }
}
