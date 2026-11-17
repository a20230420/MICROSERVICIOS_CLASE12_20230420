# Referencia del código

Cada sección indica su ubicación dentro del proyecto. Incluye configuración, fuentes, pruebas y lanzadores.

## .mvn/wrapper/maven-wrapper.properties

```properties
wrapperVersion=3.3.4
distributionType=only-script
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.11/apache-maven-3.9.11-bin.zip
distributionSha256Sum=0d7125e8c91097b36edb990ea5934e6c68b4440eef4ea96510a0f6815e7eeadb
```

## addition-service/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.example.clase12</groupId>
    <artifactId>microservicios-basico</artifactId>
    <version>1.0.0</version>
  </parent>
  <artifactId>addition-service</artifactId>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
```

## addition-service/src/main/java/com/example/clase12/addition/AdditionController.java

```java
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
```

## addition-service/src/main/java/com/example/clase12/addition/AdditionServiceApplication.java

```java
package com.example.clase12.addition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class AdditionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdditionServiceApplication.class, args);
    }
}
```

## addition-service/src/main/resources/application.properties

```properties
spring.application.name=addition-service
server.port=8010
management.endpoints.web.exposure.include=health,info
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.hostname=localhost
eureka.instance.prefer-ip-address=true
eureka.instance.ip-address=127.0.0.1
# Intervalos cortos para las pruebas en una sola PC.
eureka.client.registry-fetch-interval-seconds=5
eureka.client.initial-instance-info-replication-interval-seconds=5
eureka.instance.lease-renewal-interval-in-seconds=5
```

## addition-service/src/test/java/com/example/clase12/addition/AdditionControllerTest.java

```java
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
```

## calculator-service/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.example.clase12</groupId>
    <artifactId>microservicios-basico</artifactId>
    <version>1.0.0</version>
  </parent>
  <artifactId>calculator-service</artifactId>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-loadbalancer</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
```

## calculator-service/src/main/java/com/example/clase12/calculator/AdditionClient.java

```java
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
```

## calculator-service/src/main/java/com/example/clase12/calculator/CalculatorController.java

```java
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
```

## calculator-service/src/main/java/com/example/clase12/calculator/CalculatorServiceApplication.java

```java
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
```

## calculator-service/src/main/resources/application.properties

```properties
spring.application.name=calculator-service
server.port=8020
management.endpoints.web.exposure.include=health,info
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.hostname=localhost
eureka.instance.prefer-ip-address=true
eureka.instance.ip-address=127.0.0.1
# Intervalos cortos para las pruebas en una sola PC.
eureka.client.registry-fetch-interval-seconds=5
eureka.client.initial-instance-info-replication-interval-seconds=5
eureka.instance.lease-renewal-interval-in-seconds=5
spring.cloud.openfeign.client.config.addition-service.connectTimeout=2000
spring.cloud.openfeign.client.config.addition-service.readTimeout=3000
```

## calculator-service/src/test/java/com/example/clase12/calculator/CalculatorControllerTest.java

```java
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
```

## detener.cmd

```bat
@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\detener.ps1"
exit /b %errorlevel%
```

## ejemplos/android-lab06/app/src/debug/AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application android:networkSecurityConfig="@xml/network_security_config_microservicios"/>
</manifest>
```

## ejemplos/android-lab06/app/src/debug/res/xml/network_security_config_microservicios.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false"/>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="false">10.0.2.2</domain>
    </domain-config>
</network-security-config>
```

## ejemplos/android-lab06/app/src/main/java/com/example/lab06_20230420/CalculatorHttpClient.java

```java
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
```

## ejemplos/android-lab06/app/src/main/java/com/example/lab06_20230420/MicroserviciosActivity.java

```java
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
```

## ejemplos/android-lab06/app/src/main/res/layout/activity_microservicios.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="24dp">
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/mc_titulo"
            android:textSize="24sp"
            android:textStyle="bold"/>
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:text="@string/mc_explicacion"/>
        <EditText
            android:id="@+id/mc_numero_a"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:minHeight="48dp"
            android:hint="@string/mc_numero_a"
            android:inputType="numberSigned"
            android:importantForAutofill="no"
            android:selectAllOnFocus="true"
            android:text="1"/>
        <EditText
            android:id="@+id/mc_numero_b"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:minHeight="48dp"
            android:hint="@string/mc_numero_b"
            android:inputType="numberSigned"
            android:importantForAutofill="no"
            android:selectAllOnFocus="true"
            android:text="5"/>
        <Button
            android:id="@+id/mc_calcular"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/mc_calcular"/>
        <TextView
            android:id="@+id/mc_resultado"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginVertical="20dp"
            android:accessibilityLiveRegion="polite"
            android:text="@string/mc_listo"
            android:textIsSelectable="true"
            android:textSize="18sp"/>
        <Button
            android:id="@+id/mc_volver"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/mc_volver"/>
    </LinearLayout>
</ScrollView>
```

## ejemplos/android-lab06/app/src/main/res/values/strings_microservicios.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="mc_abrir">Probar microservicios</string>
    <string name="mc_titulo">Prueba de microservicios</string>
    <string name="mc_explicacion">Android envía dos enteros a Calculator. Calculator encuentra Addition en Eureka y le pide la suma.</string>
    <string name="mc_numero_a">Primer número entero</string>
    <string name="mc_numero_b">Segundo número entero</string>
    <string name="mc_calcular">Sumar en el servidor</string>
    <string name="mc_volver">Volver a mis sesiones</string>
    <string name="mc_listo">Inicia el backend en la PC y pulsa el botón.</string>
    <string name="mc_enteros">Escribe dos enteros entre -2147483648 y 2147483647.</string>
    <string name="mc_consultando">Consultando al servidor…</string>
    <string name="mc_error">No se completó la consulta: %1$s</string>
</resources>
```

## eureka-server/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.example.clase12</groupId>
    <artifactId>microservicios-basico</artifactId>
    <version>1.0.0</version>
  </parent>
  <artifactId>eureka-server</artifactId>
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
```

## eureka-server/src/main/java/com/example/clase12/eureka/EurekaServerApplication.java

```java
package com.example.clase12.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

## eureka-server/src/main/resources/application.properties

```properties
spring.application.name=eureka-server
server.port=8761
management.endpoints.web.exposure.include=health,info
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
# Solo acelera el ejemplo local: el registro sigue siendo eventual.
eureka.server.response-cache-update-interval-ms=1000
```

## hello-service/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.example.clase12</groupId>
    <artifactId>microservicios-basico</artifactId>
    <version>1.0.0</version>
  </parent>
  <artifactId>hello-service</artifactId>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
```

## hello-service/src/main/java/com/example/clase12/hello/HelloController.java

```java
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
```

## hello-service/src/main/java/com/example/clase12/hello/HelloServiceApplication.java

```java
package com.example.clase12.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class HelloServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloServiceApplication.class, args);
    }
}
```

## hello-service/src/main/resources/application.properties

```properties
spring.application.name=hello-service
server.port=8001
management.endpoints.web.exposure.include=health,info
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.hostname=localhost
eureka.instance.prefer-ip-address=true
eureka.instance.ip-address=127.0.0.1
# Intervalos cortos para las pruebas en una sola PC.
eureka.client.registry-fetch-interval-seconds=5
eureka.client.initial-instance-info-replication-interval-seconds=5
eureka.instance.lease-renewal-interval-in-seconds=5
```

## iniciar.cmd

```bat
@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\iniciar.ps1" %*
exit /b %errorlevel%
```

## peticiones.http

```http
### Registro Eureka: abrir en navegador
GET http://localhost:8761/

### Saludo independiente
GET http://localhost:8001/greeting

### Suma directa
GET http://localhost:8010/addition/1/5

### Calculator delega por Feign + Eureka
GET http://localhost:8020/addcalculator/1/5

### Negativos
GET http://localhost:8020/addcalculator/-8/3

### Validacion automatica: HTTP 400
GET http://localhost:8020/addcalculator/texto/3

### Registro de Addition
GET http://localhost:8761/eureka/apps/addition-service
Accept: application/json
```

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.9</version>
    <relativePath/>
  </parent>
  <groupId>com.example.clase12</groupId>
  <artifactId>microservicios-basico</artifactId>
  <version>1.0.0</version>
  <packaging>pom</packaging>
  <name>Microservicios basico - Clase 12</name>
  <properties>
    <java.version>17</java.version>
    <spring-cloud.version>2025.1.4</spring-cloud.version>
  </properties>
  <modules>
    <module>eureka-server</module>
    <module>hello-service</module>
    <module>addition-service</module>
    <module>calculator-service</module>
  </modules>
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-dependencies</artifactId>
        <version>${spring-cloud.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
</project>
```

## probar.cmd

```bat
@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\probar.ps1"
exit /b %errorlevel%
```

## scripts/comun.ps1

```powershell
$ErrorActionPreference = 'Stop'
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$RuntimeDir = Join-Path $ProjectRoot '.run'
$StateFile = Join-Path $RuntimeDir 'procesos.json'
$Services = @(
    @{ Name = 'eureka-server'; Port = 8761 },
    @{ Name = 'hello-service'; Port = 8001 },
    @{ Name = 'addition-service'; Port = 8010 },
    @{ Name = 'calculator-service'; Port = 8020 }
)

function Get-JarPath([string]$Name) {
    Join-Path $ProjectRoot "$Name\target\$Name-1.0.0.jar"
}

function Get-OwnedProcess($Entry) {
    $process = Get-CimInstance Win32_Process -Filter "ProcessId = $($Entry.ProcessId)" -ErrorAction SilentlyContinue
    $jar = Get-JarPath $Entry.Name
    if ($process -and $process.Name -eq 'java.exe' -and
        $process.CommandLine -and $process.CommandLine.Contains($jar)) {
        return $process
    }
    return $null
}

function Wait-Http([string]$Url, [int]$Seconds = 120) {
    $deadline = (Get-Date).AddSeconds($Seconds)
    do {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 3
            if ($response.StatusCode -eq 200) { return }
        } catch { }
        Start-Sleep -Seconds 2
    } while ((Get-Date) -lt $deadline)
    throw "No respondio $Url. Revisa los archivos de .run."
}
```

## scripts/detener.ps1

```powershell
. "$PSScriptRoot\comun.ps1"
if (!(Test-Path -LiteralPath $StateFile)) {
    Write-Host 'No hay procesos registrados por este proyecto.'
    return
}
$entries = Get-Content -LiteralPath $StateFile -Raw | ConvertFrom-Json
foreach ($entry in $entries) {
    if (Get-OwnedProcess $entry) {
        Stop-Process -Id $entry.ProcessId -ErrorAction Stop
        Write-Host "Detenido: $($entry.Name)"
    } else {
        Write-Host "Ya no esta activo: $($entry.Name)"
    }
}
Remove-Item -LiteralPath $StateFile
```

## scripts/iniciar.ps1

```powershell
param([switch]$SinCompilar)
. "$PSScriptRoot\comun.ps1"

if (Test-Path -LiteralPath $StateFile) {
    $entries = Get-Content -LiteralPath $StateFile -Raw | ConvertFrom-Json
    foreach ($entry in $entries) {
        if (Get-OwnedProcess $entry) {
            throw 'El ejemplo ya esta iniciado. Usa detener.cmd antes de iniciarlo otra vez.'
        }
    }
}
foreach ($service in $Services) {
    if (Get-NetTCPConnection -LocalPort $service.Port -State Listen -ErrorAction SilentlyContinue) {
        throw "El puerto $($service.Port) esta ocupado. Cierra esa aplicacion o cambia su puerto."
    }
}
if ($env:JAVA_HOME) {
    $java = Join-Path $env:JAVA_HOME 'bin\java.exe'
} else {
    $java = (Get-Command java.exe -ErrorAction Stop).Source
}
if (!(Test-Path -LiteralPath $java)) { throw 'Revisa JAVA_HOME: no se encontro java.exe.' }
Write-Host 'Java seleccionado:' $java
& $java -version
if (!$SinCompilar) {
    Push-Location $ProjectRoot
    try {
        & '.\mvnw.cmd' -B -ntp verify
        if ($LASTEXITCODE -ne 0) { throw 'Fallo Maven. Lee el primer ERROR de la compilacion.' }
    } finally { Pop-Location }
}
foreach ($service in $Services) {
    if (!(Test-Path -LiteralPath (Get-JarPath $service.Name))) {
        throw 'Faltan los JAR. Ejecuta iniciar.cmd para compilar primero.'
    }
}
New-Item -ItemType Directory -Force -Path $RuntimeDir | Out-Null
$started = @()
try {
    foreach ($service in $Services) {
        $jar = Get-JarPath $service.Name
        $process = Start-Process -FilePath $java -ArgumentList @('-Xms64m', '-Xmx256m', '-jar', "`"$jar`"") `
            -WorkingDirectory $ProjectRoot -WindowStyle Hidden -PassThru `
            -RedirectStandardOutput (Join-Path $RuntimeDir "$($service.Name).log") `
            -RedirectStandardError (Join-Path $RuntimeDir "$($service.Name).error.log")
        $started += [pscustomobject]@{ Name = $service.Name; ProcessId = $process.Id; Port = $service.Port }
        ConvertTo-Json -InputObject @($started) | Set-Content -LiteralPath $StateFile -Encoding UTF8
        Write-Host "Iniciando $($service.Name) en $($service.Port)..."
        Wait-Http "http://localhost:$($service.Port)/actuator/health"
    }
    Write-Host 'Esperando que Calculator descubra Addition mediante Eureka...'
    Wait-Http 'http://localhost:8020/addcalculator/1/5'
    Write-Host 'LISTO. Eureka: http://localhost:8761'
    Write-Host 'Prueba: http://localhost:8020/addcalculator/1/5'
    Write-Host 'Para detener todo: .\detener.cmd'
} catch {
    foreach ($entry in $started) {
        if (Get-OwnedProcess $entry) { Stop-Process -Id $entry.ProcessId -ErrorAction SilentlyContinue }
    }
    throw
}
```

## scripts/probar.ps1

```powershell
. "$PSScriptRoot\comun.ps1"
$script:checks = 0
function Check([string]$Url, [string]$Expected) {
    $result = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 10
    if ($result.StatusCode -ne 200 -or $result.Content.Trim() -ne $Expected) {
        throw "Respuesta inesperada en $Url : $($result.Content)"
    }
    $script:checks++
    Write-Host "OK $Url"
}
foreach ($service in $Services) {
    $health = Invoke-RestMethod "http://localhost:$($service.Port)/actuator/health" -TimeoutSec 10
    if ($health.status -ne 'UP') { throw "No esta listo $($service.Name)" }
    $script:checks++
}
foreach ($name in @('hello-service','addition-service','calculator-service')) {
    $registration = Invoke-RestMethod "http://localhost:8761/eureka/apps/$name" -Headers @{ Accept = 'application/json' } -TimeoutSec 10
    if (@($registration.application.instance | Where-Object status -eq 'UP').Count -lt 1) {
        throw "No esta registrado $name"
    }
    $script:checks++
}
Check 'http://localhost:8001/greeting' "Hello from 'hello-service'!"
Check 'http://localhost:8010/addition/1/5' '(1 + 5) = 6'
Check 'http://localhost:8020/addcalculator/1/5' 'Calculator : (1 + 5) = 6'
Check 'http://localhost:8020/addcalculator/-8/3' 'Calculator : (-8 + 3) = -5'
Check 'http://localhost:8010/addition/2147483647/1' '(2147483647 + 1) = 2147483648'
$code = 0
try {
    $response = Invoke-WebRequest 'http://localhost:8020/addcalculator/texto/3' -UseBasicParsing -TimeoutSec 10
    $code = [int]$response.StatusCode
} catch {
    if ($_.Exception.Response) { $code = [int]$_.Exception.Response.StatusCode }
    else { throw }
}
if ($code -ne 400) { throw "Se esperaba HTTP 400 para texto en lugar de numero; recibido $code" }
$script:checks++
Write-Host "$script:checks comprobaciones HTTP correctas."
```
