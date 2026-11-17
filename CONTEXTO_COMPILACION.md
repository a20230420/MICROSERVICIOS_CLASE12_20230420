# Contexto de compilación y ejecución

Este archivo describe el proyecto para diagnosticarlo en otra PC. Leer el primer error real antes de cambiar versiones.

## Proyecto

- Backend Java 17, Maven con cuatro módulos: `eureka-server`, `hello-service`, `addition-service`, `calculator-service`.
- Parent del POM raíz: Spring Boot **4.0.9**.
- BOM importado: Spring Cloud **2025.1.4**, compatible con Boot **4.0.x**.
- Wrapper Maven **3.3.4**, distribución **3.9.11**, checksum en `.mvn/wrapper/maven-wrapper.properties`.
- `java.version=17`. No usa Gradle, AGP, Android SDK ni archivos de Firebase.
- Calculator depende del proveedor mediante `@FeignClient(name="addition-service")`; tiene OpenFeign y LoadBalancer. Addition tiene exactamente ese `spring.application.name`.
- Registro local en `http://localhost:8761/eureka/`. Hello 8001, Addition 8010, Calculator 8020.
- Los intervalos de Eureka están acortados para una demostración local. No son una configuración propuesta para producción.
- `ejemplos/android-lab06/` contiene archivos opcionales, no un quinto módulo Maven. Se copian al proyecto Android separado siguiendo `docs/INTEGRAR_CON_LAB06.md`.
- El Lab06 conserva AGP 9.0.0, Gradle 9.1.0, Java de código 11, minSdk 34 y compile/targetSdk 36. El cliente HTTP no requiere modificar sus dependencias.

## Comandos que aportan evidencia

Desde la raíz:

```powershell
java -version
.\mvnw.cmd -version
.\mvnw.cmd -B -ntp verify
.\iniciar.cmd -SinCompilar
.\probar.cmd
.\detener.cmd
```

Si falla, conservar la salida desde el primer `[ERROR]` y el bloque `Caused by`. Los logs de ejecución están en `.run/<servicio>.log` y `.run/<servicio>.error.log`.

## Qué configuración revisar según el mensaje

| Mensaje o síntoma | Revisar |
|---|---|
| `release version 17 not supported` | Maven está usando un JDK viejo. Seleccionar JDK 17 en `JAVA_HOME` y en el IDE. |
| `UnsupportedClassVersionError` | Se está ejecutando el JAR con Java anterior a 17. |
| Boot no compatible con Cloud | Mantener Boot 4.0.x con Cloud 2025.1.x; no saltarse el verificador. |
| `Could not transfer artifact`, PKIX, proxy | Red, certificado o proxy de Maven del laboratorio; no cambiar código Java al azar. |
| Puerto ocupado | Detener la otra instancia o cambiar `server.port` del servicio afectado. |
| No se puede conectar a 8761 | Iniciar Eureka primero y revisar su log. |
| HTTP 503 en Calculator | Addition debe estar UP en Eureka; esperar el registro y revisar que sus nombres coincidan. |
| HTTP 404 | Ruta incorrecta: usar `/addcalculator/1/5` o `/addition/1/5`, respectivamente. |
| HTTP 400 | La ruta recibe enteros, no texto ni decimales. |
| POM rojo en IntelliJ | Importar el POM raíz y recargar Maven con JDK 17. |

Si se cambia el puerto de Eureka, también hay que cambiar `eureka.client.service-url.defaultZone` en los tres clientes. Si se cambian puertos para usar los scripts, actualizar `$Services` en `scripts/comun.ps1`, las URLs fijas de espera en `scripts/iniciar.ps1`, las pruebas y la documentación.

La prueba inicial en esta PC se realizó con Temurin JDK 17.0.19, Boot 4.0.9 y Cloud 2025.1.4. Esto no garantiza que una PC sin internet o sin JDK 17 pueda descargar y ejecutar dependencias; los comandos anteriores identifican ese problema.
