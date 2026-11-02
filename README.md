# AquíHay — Proyecto Android con Kotlin + Jetpack Compose

**Aplicación de demostración para buscar artículos en comercios cercanos (según colonia elegida).**

## Requisitos para abrirla

- Android Studio actualizado, con Android SDK **API 36** instalado (SDK Manager).
- JDK **17** configurado para Gradle (Android Studio incluye normalmente un JDK compatible).
- Conexión a internet **solo para descargar Gradle y dependencias de compilación la primera vez**.
- Un emulador Android o teléfono con Android **8.0 / API 26** o superior.

## Cómo ejecutar

1. Extrae el ZIP y abre **Android Studio**.
2. Elige **File > Open** y selecciona la carpeta `AquiHayAndroid` (la que contiene `settings.gradle.kts`).
3. Comprueba en **Settings > Build, Execution, Deployment > Build Tools > Gradle** que se use un JDK 17 compatible.
4. Instala el SDK Android 36 desde **Tools > SDK Manager** si hace falta.
5. Sincroniza con **Sync Project with Gradle Files**.
6. Selecciona tu emulador o teléfono y pulsa **Run ▶**.

**Nota sobre el Gradle Wrapper:** este paquete no adjunta el binario `gradle-wrapper.jar`. Si Android Studio avisa de que falta, abre la terminal en la carpeta del proyecto y ejecuta `./gradlew help` (en macOS/Linux) o `gradlew.bat help` (en Windows). Estos scripts descargan el JAR oficial del repositorio de Gradle 8.13 y luego descargan la distribución de Gradle. Después vuelve a sincronizar el proyecto. Si no tienes conexión, puedes generar el wrapper manualmente con una instalación local de Gradle 8.13.

## Funciones incluidas que sí realizan acciones

| Área | Función | Resultado |
|---|---|---|
| Buscar | Escribir un producto o comercio | Resultados filtrados al instante, sin tildes ni distinción de mayúsculas |
| Buscar | Seleccionar categoría | Muestra solo los productos de esa categoría |
| Buscar | Elegir colonia o zona | Muestra productos de los comercios de esa zona |
| Buscar | Activar «Solo con existencias» | Oculta productos con stock cero |
| Buscar | Ordenar por precio o nombre | Reordena las tarjetas de resultados |
| Buscar | Pulsar una tarjeta | Abre detalles de producto, stock y local |
| Comercios | Pulsar un local | Muestra horario, dirección y su lista de productos |
| Detalle | Abrir en Maps | Envía la dirección o zona a Google Maps mediante un navegador/app; requiere internet |
| Inventario | Agregar o editar comercio | Guarda el local en el dispositivo |
| Inventario | Eliminar comercio | Pide confirmación y elimina también sus artículos |
| Inventario | Agregar o editar producto | Valida y almacena precio, categoría, stock, etc. |
| Inventario | Eliminar producto | Pide confirmación y lo retira del catálogo |
| Inventario | Restablecer demo | Pide confirmación y restaura los datos iniciales |

## Prueba manual recomendada

1. En **Buscar**, escribe `pilas`. Deben aparecer cuatro productos del catálogo de ejemplo.
2. Activa **Solo con existencias**. Los resultados con inventario en cero se ocultan.
3. Cambia la ordenación a **Menor precio**: la primera tarjeta debe ser la más económica.
4. En **Inventario**, selecciona un comercio, pulsa **Producto** y guarda un artículo nuevo válido.
5. Intenta ingresar un precio negativo, un nombre vacío o existencias decimales: debe mostrar un mensaje de validación.
6. Vuelve a **Buscar** y busca el producto nuevo. Debe aparecer.
7. Cierra y reabre la aplicación. El artículo agregado debe conservarse.
8. Pulsa **Restablecer datos de demostración**, confirma y vuelve a buscar: los datos iniciales deben recuperarse.

### Pruebas automatizadas

El proyecto incluye pruebas JUnit para la lógica de búsqueda, filtros, ordenación y validaciones:

```bash
./gradlew testDebugUnitTest
```

En el entorno de elaboración del proyecto se ejecutaron además **10 pruebas independientes** de la lógica pura de Kotlin (sin Android): **10/10 satisfactorias**. **No se pudo generar ni ejecutar un APK aquí**, debido a que no hay Android SDK ni Gradle preinstalados en este entorno. Es necesario sincronizar/compilar en Android Studio para comprobar la integración visual y de plataforma.

## Alcance real de esta entrega

- **Modo demostración local.** Cuatro establecimientos y 12 productos **ficticios** vienen cargados para que puedas usar la app sin conexión.
- **Los cambios se guardan localmente** en `SharedPreferences` como JSON. No se comparten con otros teléfonos o usuarios.
- **Sin GPS, sin mapa embebido y sin distancias calculadas.** La búsqueda por cercanía se aproxima con la *colonia* seleccionada manualmente. Google Maps se abre **fuera de la app** cuando pulsas el botón correspondiente.
- **Sin inventario verificado.** Las existencias iniciales son ficticias y se identifican como tales. Para usar datos reales se necesitaría integrar comercios reales y un mecanismo de actualización de stock.
- **Sin usuarios ni autenticación.** La pestaña Inventario está abierta para demostrar el CRUD; no sería adecuada para producción sin permisos de comerciante.
- **Sin compras, pagos, pedidos, notificaciones ni servidor.** No hay botones engañosos que aparenten realizar esas funciones.

## Estructura

```
app/src/main/java/com/aquihay/app/
  MainActivity.kt
  model/Models.kt
  model/SearchAndValidation.kt   // búsqueda, filtros y validaciones puras
  data/InventoryRepository.kt    // datos demo y persistencia local JSON
  ui/Theme.kt
  ui/AquiHayApp.kt               // estado, navegación inferior y guardado
  ui/SearchScreen.kt
  ui/ShopsScreen.kt
  ui/InventoryScreen.kt
  ui/Components.kt
  ui/Dialogs.kt
app/src/test/java/com/aquihay/app/model/SearchAndValidationTest.kt
```

## Configuración de compilación

- Android Gradle Plugin: `8.13.2`
- Gradle: `8.13`
- Kotlin + plugin Compose: `2.2.21`
- Compose BOM: `2025.10.00`
- Activity Compose: `1.11.0`
- Android: `compileSdk 36`, `targetSdk 35`, `minSdk 26`

Referencias técnicas oficiales:
- https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler
- https://developer.android.com/build/releases/gradle-plugin
- https://developer.android.com/develop/ui/compose/bom

## Ideas para versión 2

Conectar los comercios a una base de datos central, verificar propietarios, sincronizar existencias automáticamente y habilitar geolocalización GPS con mapas reales de establecimientos. Estas funciones **no forman parte de la entrega actual**.
