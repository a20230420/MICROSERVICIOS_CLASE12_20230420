# Guía de microservicios desde cero

## 1. Qué estás ejecutando

Un servicio es una aplicación que recibe peticiones y devuelve respuestas. En este ejemplo se ejecutan cuatro aplicaciones Java separadas, cada una con su puerto. Puedes apagar Addition y Hello seguirá respondiendo. Calculator seguirá encendido, pero no podrá completar una suma porque depende de Addition.

| Nombre | Trabajo | Archivo principal |
|---|---|---|
| Eureka | Guardar qué servicios están disponibles y dónde | `EurekaServerApplication.java` |
| Hello | Responder un saludo | `HelloController.java` |
| Addition | Hacer la suma | `AdditionController.java` |
| Calculator | Recibir la solicitud y pedirle el resultado a Addition | `CalculatorController.java`, `AdditionClient.java` |

Puedes tener los cuatro procesos en una PC para practicar. Separar procesos y responsabilidades es lo que demuestra este ejemplo; no necesitas cuatro computadoras.

## 2. Lo que significan REST, endpoint y GET

Un endpoint es una combinación de método HTTP y ruta que atiende el servidor. Al abrir `http://localhost:8010/addition/1/5` en el navegador haces un GET. `localhost` es tu propia PC; `8010` selecciona Addition; `/addition/1/5` identifica la operación y sus dos valores.

| Método | Uso habitual |
|---|---|
| GET | Consultar |
| POST | Crear o enviar una operación con datos |
| PUT | Actualizar o reemplazar un recurso |
| DELETE | Eliminar |

La suma usa GET como en clase. Una app que guarde sesiones podría usar POST para crearlas; esa integración todavía no forma parte de este ejemplo.

## 3. Antes de ejecutar en la PC del laboratorio

1. Copia o clona **la carpeta completa**, incluyendo el directorio oculto `.mvn` y los scripts `mvnw` y `mvnw.cmd`.
2. Abre una terminal en esa carpeta. Debes ver el `pom.xml` raíz y las cuatro carpetas de servicios.
3. Ejecuta `java -version`. La referencia probada es JDK 17. El Android SDK y el emulador no se usan para compilar este backend.
4. Ejecuta `.\mvnw.cmd -version`. El wrapper descarga Maven en tu carpeta de usuario; la primera vez necesita conexión.
5. Ejecuta `.\iniciar.cmd` y espera `LISTO`.

Si Java no aparece o Maven usa otra versión, selecciona la instalación real de JDK 17. Este ejemplo de PowerShell afecta solo a la terminal actual; sustituye la ruta por la que exista en esa PC:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
.\mvnw.cmd -version
```

En IntelliJ configura también Project SDK y Maven Runner JRE con ese JDK. No cambies las versiones del POM solo porque el IDE aún está importando dependencias.

## 4. Primera demostración

Abre estas direcciones después de que termine el arranque:

1. `http://localhost:8761`: Eureka muestra HELLO-SERVICE, ADDITION-SERVICE y CALCULATOR-SERVICE con estado UP. Los nombres pueden aparecer en mayúsculas.
2. `http://localhost:8001/greeting`: responde el saludo.
3. `http://localhost:8010/addition/1/5`: responde `(1 + 5) = 6`.
4. `http://localhost:8020/addcalculator/1/5`: responde `Calculator : (1 + 5) = 6`.
5. Cambia `1/5` por `20/8`: Calculator debe responder `Calculator : (20 + 8) = 28`.

Luego ejecuta `.\probar.cmd`. Comprueba 13 condiciones HTTP: cuatro estados de salud, tres registros, cinco respuestas y una validación de entrada inválida.

Para terminar ejecuta `.\detener.cmd`. Cerrar el navegador o la terminal no apaga los procesos iniciados en segundo plano.

## 5. Seguir una petición paso a paso

```text
Navegador: GET localhost:8020/addcalculator/1/5
  -> CalculatorController recibe a=1 y b=5
  -> AdditionClient solicita addition(1, 5)
  -> Feign + LoadBalancer resuelven addition-service con el registro de Eureka
  -> HTTP GET localhost:8010/addition/1/5
  -> AdditionController calcula y responde (1 + 5) = 6
  -> Calculator agrega su prefijo y devuelve la respuesta
```

Calculator consulta una copia local del registro que actualiza periódicamente; no tiene que preguntar a Eureka en cada operación. Eureka no recibe la petición de suma para reenviarla. Los clientes se comunican directamente una vez resuelta la dirección.

## 6. Cómo está armado el POM

El `pom.xml` raíz concentra versiones y enumera módulos. Cada módulo tiene su propio POM, su clase con `main()` y su `application.properties`.

- **Spring Boot parent:** define versiones de muchas librerías y plugins.
- **Spring Cloud BOM:** alinea las versiones de Eureka, Feign y LoadBalancer. En las dependencias de estos componentes no pongas versiones individuales al azar.
- **Java 17:** nivel de compilación del backend.
- **spring-boot-maven-plugin:** empaqueta un JAR ejecutable con sus dependencias.

| Módulo | Dependencias principales |
|---|---|
| Eureka | `spring-cloud-starter-netflix-eureka-server` |
| Hello y Addition | `spring-boot-starter-webmvc`, `spring-cloud-starter-netflix-eureka-client` |
| Calculator | WebMVC, Eureka Client, `spring-cloud-starter-openfeign`, `spring-cloud-starter-loadbalancer` |
| Los cuatro | Actuator para `/actuator/health`, Starter Test para pruebas |

Este proyecto usa Maven: aquí editas `pom.xml`. Los archivos `build.gradle` y la versión de AGP corresponden al proyecto Android, que tiene una cadena de compilación distinta.

## 7. Anotaciones que debes reconocer

| Anotación | Para qué sirve |
|---|---|
| `@SpringBootApplication` | Inicia y configura una aplicación Spring Boot |
| `@EnableEurekaServer` | Habilita el servidor de registro |
| `@RestController` | Hace que los métodos atiendan peticiones y devuelvan contenido HTTP |
| `@GetMapping` | Asocia un método Java con una ruta GET |
| `@PathVariable` | Toma un valor de la ruta URL |
| `@EnableFeignClients` | Habilita las interfaces Feign de la aplicación |
| `@FeignClient(name="addition-service")` | Declara a qué servicio debe llamar la interfaz |

Con Eureka Client como dependencia y la configuración correspondiente, el cliente se registra automáticamente; este ejemplo no necesita `@EnableDiscoveryClient` explícito.

## 8. Cómo cambiar algo sin perderte

**Cambiar el saludo:** modifica el texto en `HelloController.greeting()`. Detén los servicios, ejecuta `iniciar.cmd` otra vez y abre `/greeting`.

**Cambiar el formato del resultado:** modifica el `return` de `AdditionController`. Recuerda ajustar las pruebas que verifican ese texto.

**Cambiar el nombre del proveedor:** si cambias `spring.application.name` de Addition, cambia también el `name` de `@FeignClient` y el nombre de su configuración de timeouts. Si no coinciden, Calculator no lo encontrará.

**Agregar una resta como ejercicio:**

1. Copia el patrón de Addition en un módulo nuevo `subtraction-service`, con paquete Java propio.
2. Añade ese módulo en `<modules>` del POM raíz.
3. Define `spring.application.name=subtraction-service`, otro puerto libre, por ejemplo 8030, y la misma URL de Eureka.
4. Implementa `GET /subtraction/{item_a}/{item_b}` y calcula `(long) a - b`.
5. En Calculator crea otra interfaz `@FeignClient(name="subtraction-service")` con la ruta correspondiente.
6. Agrega un endpoint `/subtractcalculator/{item_a}/{item_b}` que use ese cliente.
7. Compila, inicia el nuevo servicio y prueba primero su ruta directa; luego prueba la ruta de Calculator.

Los lanzadores conocen cuatro servicios. Para incluir el quinto, actualiza `scripts/comun.ps1`, sus pruebas y documentación; también puedes ejecutarlo manualmente.

## 9. Iniciar manualmente, sin el lanzador

Primero compila desde la raíz:

```powershell
.\mvnw.cmd -B -ntp verify
```

Abre cuatro terminales en la misma raíz y ejecuta un comando en cada una, empezando por Eureka:

```powershell
# Terminal 1
java -jar .\eureka-server\target\eureka-server-1.0.0.jar
```

```powershell
# Terminal 2
java -jar .\hello-service\target\hello-service-1.0.0.jar
```

```powershell
# Terminal 3
java -jar .\addition-service\target\addition-service-1.0.0.jar
```

```powershell
# Terminal 4
java -jar .\calculator-service\target\calculator-service-1.0.0.jar
```

Cada terminal muestra su log. Detén cada servicio con Ctrl+C. En esta modalidad `detener.cmd` no los controla: ese comando solo conoce los procesos de su propio lanzador.

También puedes ejecutar los cuatro métodos `main()` desde IntelliJ, esperando a que Eureka esté listo antes de los clientes. Usa una modalidad a la vez para evitar puertos duplicados.

## 10. Entender una caída

Con la modalidad de cuatro terminales, detén Addition con Ctrl+C. Hello seguirá respondiendo; Calculator devolverá HTTP 503 y explicará que Addition no está disponible. Es una dependencia remota caída: Calculator no inventa una suma exitosa.

Vuelve a iniciar Addition, espera a que aparezca UP en Eureka y prueba otra vez. Puede tardar unos segundos por las actualizaciones y cachés del registro. Eureka puede seguir mostrando una instancia detenida durante un tiempo; es un registro con latidos y expiración, no una lista de procesos instantánea.

Este ejemplo maneja el error con HTTP 503 y timeouts. No implementa un circuit breaker ni un mecanismo de reintentos avanzado; Feign y Eureka no equivalen por sí solos a esos patrones.

## 11. Si después te piden conectarlo a Android

Ya tienes una extensión opcional en `ejemplos/android-lab06/`: una pantalla, cliente HTTP y configuración de red debug. Consulta la guía `docs/INTEGRAR_CON_LAB06.md` para copiarla al Lab06 publicado, conservar sus versiones y probar la suma desde Android. La pantalla no se incorpora automáticamente al proyecto original.

La app Android sería un cliente HTTP que llama a Calculator y transforma su respuesta. Primero confirma qué pide el nuevo enunciado: formato JSON, operaciones, autenticación y dónde se desplegarán los servicios.

En un emulador estándar, `localhost` se refiere al emulador. Para llegar a la PC anfitriona se suele usar `http://10.0.2.2:8020/...`. En un celular físico se usa la dirección de la PC en la red, con conectividad y firewall adecuados. Android puede exigir una configuración explícita de tráfico HTTP de desarrollo; se revisa al integrar la app, no cambiando el backend a ciegas.

Firebase del Lab6 resuelve autenticación y datos. Eureka resuelve descubrimiento de servicios. No son intercambiables. Este demo no valida tokens de Firebase ni accede a las sesiones del Lab6; esa integración requiere requisitos y cambios adicionales.

## 12. Lista rápida para estudiar

Debes poder explicar con tus palabras:

- Qué aplicación recibe primero la solicitud de Calculator.
- En qué archivo se realiza realmente la suma.
- Por qué Calculator usa un nombre de servicio en lugar del puerto 8010.
- Para qué sirve Eureka y por qué no es quien suma ni reenvía cada petición.
- Qué ocurre cuando Addition se apaga.
- Qué significan 200, 400, 404 y 503.
- Qué cambias en un POM, qué cambias en properties y qué cambias en un controller.

La referencia completa del código está junto a esta guía. Para problemas de compilación, consulta `CONTEXTO_COMPILACION.md` y conserva el primer error real.
