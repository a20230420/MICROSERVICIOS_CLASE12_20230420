# Archivos opcionales para el Lab06 Android

Estos archivos se copian **dentro de la app Android LAB6_20230420**, paquete `com.example.lab06_20230420`. Esta carpeta no es un proyecto Android independiente ni un módulo Maven.

La pantalla usa las clases existentes `PantallaPrivada` y `SesionRepository` del Lab06. Conserva su acceso con Firebase; la petición a Calculator es una demostración de red que no envía datos personales ni tokens.

Instrucciones completas: [../../docs/INTEGRAR_CON_LAB06.md](../../docs/INTEGRAR_CON_LAB06.md).

1. Copia los archivos de `app/src/main/java` y `app/src/main/res` respetando sus rutas.
2. Combina el manifiesto y recurso `app/src/debug` con los tuyos si ya existen. El permiso HTTP local se limita a debug y a `10.0.2.2`.
3. Registra `.MicroserviciosActivity` dentro de `<application>` del manifiesto principal.
4. Agrega el botón y listener descritos en la guía en `SesionesActivity`.
5. Inicia los cuatro servicios en la PC y ejecuta `assembleDebug` del Lab06.

No necesitas agregar dependencias ni cambiar ningún `build.gradle`, versión de AGP, wrapper, SDK o `google-services.json` para esta pantalla. El JSON local del Lab06 sigue siendo necesario para iniciar sesión en Firebase.
