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

Estos resultados iniciales corresponden al backend en esta PC. En el laboratorio deben estar disponibles JDK 17, puertos libres y acceso a las dependencias de Maven para la primera descarga. La extensión Android añadida posteriormente se verifica en la sección siguiente.

## Extensión Android opcional incorporada después

- Se clonó el Lab06 en una carpeta temporal y se incorporaron los seis archivos de `ejemplos/android-lab06/app/`, el registro de la actividad y el botón/listener descritos en la guía.
- Se comprobó que `build.gradle`, `app/build.gradle`, `settings.gradle` y el wrapper de Gradle fueran idénticos a los del Lab06 original: AGP 9.0.0, Gradle 9.1.0, minSdk 34, compile/targetSdk 36 y código Java 11.
- Gradle se ejecutó con el JBR de Android Studio: `:app:assembleDebug :app:assembleRelease :app:testDebugUnitTest` terminó en BUILD SUCCESSFUL. Pasaron las ocho pruebas unitarias existentes del Lab06.
- El manifiesto combinado debug contiene la configuración HTTP local; el manifiesto release no la contiene.
- El mismo `CalculatorHttpClient.java` se compiló con `javac --release 11` y se probó por HTTP en la JVM: suma positiva y negativa contra el backend real, y lectura del error 503 UTF-8 contra un servidor de prueba. Las tres comprobaciones pasaron.
- No se ejecutó la nueva pantalla en un dispositivo/emulador ni se repitió el login Firebase. Su compilación y su cliente de red están comprobados; los pasos para probar el flujo visual completo están en `INTEGRAR_CON_LAB06.md`.
- El proyecto Android original y su repositorio remoto se conservaron sin cambios.
