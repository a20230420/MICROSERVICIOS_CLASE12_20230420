# Validación local — 17 de noviembre de 2026

Entorno utilizado: Windows, Temurin JDK 17.0.19, Maven Wrapper 3.3.4 con Maven 3.9.11, Spring Boot 4.0.9 y Spring Cloud 2025.1.4.

- `mvnw.cmd -B -ntp verify`: BUILD SUCCESS en los cuatro módulos.
- Pruebas unitarias: 5, sin errores ni fallos. Cubren sumas positivas, negativas, desbordamiento de int, delegación al cliente remoto y respuesta 503 cuando falla.
- `iniciar.cmd -SinCompilar`: cuatro JAR independientes iniciados y primera suma remota comprobada.
- `probar.cmd`: 13 comprobaciones HTTP correctas: cuatro estados de salud, tres registros Eureka, cinco respuestas y una entrada inválida con HTTP 400.
- Caída real de Addition: Hello continuó respondiendo HTTP 200; Calculator devolvió HTTP 503.
- Recuperación de Addition: Calculator volvió a responder HTTP 200 con la suma.
- `detener.cmd`: verificó la pertenencia de cada proceso por su JAR y detuvo los cuatro servicios.
- Reinicio posterior: se comprobó nuevamente el arranque y las peticiones HTTP.

Estos resultados corresponden a esta PC. En el laboratorio deben estar disponibles JDK 17, puertos libres y acceso a las dependencias de Maven para la primera descarga. No se ha probado una integración con Android ni con Firebase, porque este ejemplo se ejecuta por separado.
