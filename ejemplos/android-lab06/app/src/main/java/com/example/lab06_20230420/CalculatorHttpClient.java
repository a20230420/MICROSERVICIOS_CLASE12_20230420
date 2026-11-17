package com.example.lab06_20230420;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Cliente Java sin dependencias adicionales. Ejecutar fuera del hilo de interfaz. */
public final class CalculatorHttpClient {
    private final String baseUrl;

    public CalculatorHttpClient(String baseUrl) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    public String sumar(int a, int b) throws IOException {
        URL url = new URL(baseUrl + "/addcalculator/" + a + "/" + b);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(6000);
            connection.setRequestProperty("Accept", "text/plain");
            int status = connection.getResponseCode();
            InputStream stream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
            StringBuilder body = new StringBuilder();
            if (stream != null) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (body.length() > 0) body.append('\n');
                        body.append(line);
                    }
                }
            }
            if (status < 200 || status >= 300) {
                throw new IOException("HTTP " + status + ": " + body);
            }
            return body.toString();
        } finally {
            connection.disconnect();
        }
    }
}
