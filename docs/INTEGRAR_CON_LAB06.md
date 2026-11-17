# Cómo partir del Lab06 para usar microservicios

## 1. Qué hay listo y qué representa

El repositorio [LAB6_20230420_ESTUDIO](https://github.com/a20230420/LAB6_20230420_ESTUDIO) contiene tu app Android de sesiones de estudio: login, Firestore, CRUD y gráficos. Este repositorio contiene el backend de Clase 12.

Se incluye una **pantalla Android opcional, con sus archivos listos para copiar**, que conecta ambos proyectos para una demostración:

```text
Lab06 Android: MicroserviciosActivity
    -> GET http://10.0.2.2:8020/addcalculator/1/5
Calculator en la PC
    -> descubre addition-service con Eureka
    -> llama por Feign a Addition
Addition devuelve (1 + 5) = 6
    -> Android muestra Calculator : (1 + 5) = 6
```

El Lab06 publicado se conserva como base. Los archivos de esta extensión están en `ejemplos/android-lab06/` de este nuevo repositorio. Debes incorporarlos a una copia o rama del proyecto Android siguiendo esta guía; no se aplican automáticamente al clonar el backend.

La demostración verifica una llamada HTTP. No migra tus sesiones a microservicios ni transforma la suma en un cálculo de horas de estudio. Más adelante se explica cómo adaptar el dominio cuando conozcas el nuevo enunciado.

## 2. Conservar las dos configuraciones de compilación

| Proyecto | Configuración que se conserva |
|---|---|
| Backend de Clase 12 | Java 17, Spring Boot 4.0.9, Spring Cloud 2025.1.4, Maven 3.9.11 por wrapper |
| Lab06 Android | Java en código 11, Groovy, AGP 9.0.0, Gradle 9.1.0, Google Services 4.4.4 |
| SDK de Lab06 | minSdk 34 (Android 14), compileSdk 36 y targetSdk 36 |
| Firebase del Lab06 | BoM 34.13.0, FirebaseUI 9.1.1, proyecto `lab6-20230420` |

La pantalla usa `HttpURLConnection` y `ExecutorService`, disponibles en Java/Android. **No se agrega ninguna dependencia ni se modifica `build.gradle` para conectarla.** El nivel Java 11 del código Android no es la versión con la que debe arrancarse Gradle: conserva el JDK de Gradle compatible con tu Android Studio. En la validación local se usó el JBR incluido con Android Studio para Gradle y JDK 17 para el backend.

Android 14 es el mínimo del dispositivo/emulador. No cambies `compileSdk` a 34 al agregar esta pantalla: las dependencias actuales del Lab06 se validaron con SDK 36.

## 3. Preparación

1. Clona o copia ambos repositorios en carpetas separadas.
2. En Android Studio abre la **raíz completa del Lab06**, donde está `settings.gradle`, y espera la sincronización.
3. Conserva localmente `app/google-services.json` del proyecto `lab6-20230420`. Descárgalo de tu Firebase si la copia recién clonada no lo tiene. La nueva pantalla hereda el acceso privado del Lab06 y requiere iniciar sesión.
4. En la raíz del backend ejecuta `iniciar.cmd` y espera `LISTO`.
5. Desde el navegador de la PC prueba `http://localhost:8020/addcalculator/1/5` antes de revisar Android.

Para trabajar en una rama del Lab06 sin modificar `main`:

```bash
git switch -c codex/prueba-microservicios
```

## 4. Copiar los archivos de la extensión

En Android Studio cambia de la vista **Android** a **Project** si necesitas ver `src/debug`.

Copia desde `ejemplos/android-lab06/` hacia la raíz de tu Lab06 manteniendo estas rutas:

| Archivo | Función |
|---|---|
| `app/src/main/java/com/example/lab06_20230420/CalculatorHttpClient.java` | Ejecuta GET, configura timeouts y lee la respuesta o el error |
| `app/src/main/java/com/example/lab06_20230420/MicroserviciosActivity.java` | Pantalla que valida dos enteros y consulta sin bloquear la interfaz |
| `app/src/main/res/layout/activity_microservicios.xml` | Campos, botón y resultado |
| `app/src/main/res/values/strings_microservicios.xml` | Textos de la pantalla |
| `app/src/debug/AndroidManifest.xml` | Activa la configuración de red solo para la variante debug |
| `app/src/debug/res/xml/network_security_config_microservicios.xml` | Permite HTTP únicamente al host del emulador `10.0.2.2` |

Si ya tienes un manifiesto debug o una configuración de red, combina sus elementos: no reemplaces otras configuraciones propias sin revisarlas. La base Lab06 de referencia no tenía esos archivos.

Los paquetes están escritos para `com.example.lab06_20230420`. Si tu app usa otro paquete, actualiza las declaraciones `package` de las dos clases. La actividad usa `PantallaPrivada`, que existe en el Lab06 publicado; este código no se pega directamente en un proyecto Empty Activity vacío.

## 5. Registrar la pantalla en el manifiesto principal

En `app/src/main/AndroidManifest.xml`, agrega esta línea **dentro de `<application>`**, junto a las otras actividades:

```xml
<activity
    android:name=".MicroserviciosActivity"
    android:exported="false"/>
```

El permiso siguiente ya existe en el Lab06; comprueba que esté fuera de `<application>`:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

No se solicita ese permiso mediante una ventana al usuario. La actividad no necesita ser launcher ni exported para abrirla desde la lista de sesiones.

## 6. Agregar un botón en la lista de sesiones

Abre `app/src/main/res/layout/activity_sesiones.xml`. Agrega este botón en la columna de contenido, por ejemplo **antes del TextView con ID `estado`**. El layout del Lab06 ya declara los espacios de nombres `android` y `app`.

```xml
<com.google.android.material.button.MaterialButton
    android:id="@+id/mc_abrir"
    style="@style/Widget.MaterialComponents.Button.OutlinedButton"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="@string/mc_abrir"
    android:textAllCaps="false"/>
```

Abre `SesionesActivity.java`. Dentro de `onCreate()`, después de `setContentView(binding.getRoot())`, añade:

```java
findViewById(R.id.mc_abrir).setOnClickListener(v ->
    startActivity(new Intent(this, MicroserviciosActivity.class)));
```

`Intent` ya está importado en la actividad del Lab06. La nueva pantalla hereda `PantallaPrivada`, comprueba la sesión de Firebase y vuelve al login si esa sesión termina.

## 7. Probar con Android 14

1. Mantén el backend encendido en la PC.
2. Ejecuta la variante **debug** del Lab06 en un emulador Android 14/API 34.
3. Inicia sesión como siempre.
4. En tu lista pulsa **Probar microservicios**.
5. Conserva 1 y 5 y pulsa **Sumar en el servidor**.
6. Debe aparecer `Calculator : (1 + 5) = 6`.
7. Prueba -8 y 3; debe aparecer `Calculator : (-8 + 3) = -5`.
8. Vuelve a tus sesiones y comprueba que siguen funcionando el CRUD y los gráficos.

La pantalla usa un hilo de trabajo para HTTP y vuelve al hilo de interfaz para actualizar el resultado. Tiene timeouts y muestra el error HTTP si Addition está apagado; no hace red directamente en `onCreate()` ni en el hilo principal.

Desde terminal en la raíz del Lab06:

```powershell
.\gradlew.bat :app:assembleDebug
```

Esa compilación comprueba que los archivos, recursos y manifiestos se integran. La prueba visual requiere además Firebase configurado, sesión iniciada y backend accesible. El backend por sí solo no sustituye el login ni el `google-services.json` de la app.

## 8. Direcciones de red y errores frecuentes

| Dónde se hace la llamada | Dirección de Calculator |
|---|---|
| Navegador de la PC del backend | `http://localhost:8020` |
| Emulador estándar de esa misma PC | `http://10.0.2.2:8020` |
| Celular físico | `http://IP_DE_LA_PC:8020`, en una red que permita comunicarlos |
| Un despliegue real | URL HTTPS del backend desplegado |

La constante está en `MicroserviciosActivity.BASE_URL`. `localhost` dentro de Android se refiere al propio dispositivo, no a tu PC. La configuración debug incluida permite solo `10.0.2.2`: para un celular físico agrega también la IP concreta de la PC como `<domain>` en ese archivo debug, y verifica conectividad y firewall. La app release conserva el bloqueo predeterminado de HTTP; para desplegar se usa HTTPS.

| Síntoma | Revisar |
|---|---|
| `CLEARTEXT communication ... not permitted` | Usar debug y copiar el manifiesto/recurso de red de debug |
| `Connection refused` | Calculator apagado, dirección o puerto equivocados |
| Timeout | Conectividad; PC, emulador y backend correctos; firewall |
| HTTP 503 | Addition no está disponible o aún no se registró |
| No encuentra `MicroserviciosActivity` | Paquete/ruta, registro en manifiesto y compilación |
| No encuentra `mc_abrir` | Falta el botón XML o se pegó en otro layout |
| Vuelve al login al abrir la pantalla | Falta una sesión válida de Firebase |
| Google Login falla en otra PC | Revisar la firma debug registrada en Firebase; no es un fallo de Feign |

## 9. Adaptarlo después a las sesiones reales del Lab06

Cuando tengas el enunciado, un camino posible es calcular un resumen de estudio en el backend:

```text
SesionesActivity / GraficosActivity
  -> usa SesionRepository para obtener las sesiones del usuario actual
  -> envía una solicitud JSON al backend autenticado
Calculator (o un servicio coordinador)
  -> descubre un nuevo study-summary-service por Eureka
  -> lo llama por Feign
StudySummaryService
  -> calcula totales y devuelve JSON
Android
  -> muestra los totales o alimenta sus gráficos
```

**Este endpoint de estudio es una propuesta, no está implementado.** El ejemplo entregado solo tiene `/greeting`, `/addition/...` y `/addcalculator/...`, además de las rutas de infraestructura.

Una propuesta de contrato podría ser:

```http
POST /api/estudio/resumen
Authorization: Bearer <ID_TOKEN_FIREBASE>
Content-Type: application/json

{
  "sesiones": [
    {"curso": "IoT", "fecha": "2026-11-17", "minutos": 90, "tipoActividad": "Laboratorio"},
    {"curso": "IoT", "fecha": "2026-11-18", "minutos": 60, "tipoActividad": "Repaso"}
  ]
}
```

Respuesta propuesta:

```json
{
  "totalSesiones": 2,
  "totalMinutos": 150,
  "porCurso": [{"curso": "IoT", "minutos": 150}]
}
```

Tu modelo real se llama `SesionEstudio` y guarda `horas` como **double**. Para convertir a minutos puedes usar `Math.round(sesion.getHoras() * 60.0)`, validando rango y valores finitos. No copies la suma de enteros para horas decimales: 1.5 horas son 90 minutos y no deben perderse al convertir primero a `int`.

Cambios concretos para ese futuro requerimiento:

1. Definir DTOs de solicitud/respuesta y validaciones del contrato.
2. Crear `study-summary-service` con Eureka Client, un puerto libre y un controller POST.
3. En Calculator crear una interfaz Feign para ese servicio y el endpoint coordinador.
4. En Android obtener la lista desde el callback de `SesionRepository.escuchar(...)`, convertirla a JSON y consumir el nuevo endpoint en un hilo de trabajo.
5. Mostrar la respuesta en una pantalla adicional o adaptar `GraficosActivity`; no reemplazar el CRUD hasta que el enunciado lo requiera.
6. Probar totales, fechas, números decimales, lista vacía, errores y usuarios distintos.

### Autenticación si se envían datos de usuarios

El login Firebase de Android **no protege automáticamente** los endpoints del backend. La suma de demostración es pública y no usa sesiones personales.

Para un endpoint que maneje datos de usuarios, Android obtiene un ID token con `FirebaseAuth.getInstance().getCurrentUser().getIdToken(false)` y lo envía por HTTPS. El backend verifica ese token con Firebase Admin y deriva de él el UID; no confía en un UID elegido por el cliente. Si el servidor consulta Firestore mediante Admin, debe aplicar la autorización por usuario explícitamente, porque Admin no se limita con las reglas del cliente.

La configuración del servidor se entrega mediante credenciales externas al repositorio. `google-services.json` es configuración del cliente Android, no reemplaza una credencial Admin. Si el laboratorio solo pide consumir un endpoint de operaciones públicas, no hace falta introducir toda esta integración con Firebase Admin.

## 10. Qué se verificó para esta extensión

La compilación de referencia se hace en una copia temporal del Lab06, incorporando estos seis archivos, el registro de la actividad y el botón/listener. El resultado queda descrito en `docs/VALIDACION.md`.

La copia temporal evita modificar o volver a publicar el Lab06 original. Los archivos listos para copiar y esta guía se publican en el nuevo repositorio de microservicios.

Referencias oficiales: [red en Android](https://developer.android.com/develop/connectivity/network-ops/connecting), [configuración de seguridad de red](https://developer.android.com/privacy-and-security/security-config), [direcciones del emulador](https://developer.android.com/studio/run/emulator-networking-address), [verificar ID tokens Firebase](https://firebase.google.com/docs/auth/admin/verify-id-tokens), [Admin y reglas Firestore](https://firebase.google.com/docs/firestore/security/rules-conditions#authentication).
