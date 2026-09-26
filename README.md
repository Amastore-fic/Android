# CompraSegura · Android

App nativa en Kotlin (Views + Material 3) para el **cliente (comprador)**. Las funciones de vendedor y administrador viven solo en el panel web.

## Arrancar

1. Abrir esta carpeta en Android Studio (sincroniza Gradle solo).
2. Levantar la API (`api/`, en `http://localhost:8000`).
3. Correr la app en el emulador. En debug apunta a `http://10.0.2.2:8000/api/v1/` (el `localhost` de la PC visto desde el emulador).
   En un teléfono físico, cambia `API_URL` del bloque `debug` en `app/build.gradle.kts` por la IP de tu PC y agrégala a `res/xml/network_security_config.xml`.

Cuenta de prueba: `cliente@comprasegura.test` / `password123`.

## Estructura

```
app/src/main/java/com/example/comprasegura/
  MainActivity.kt          Catálogo (pantalla de entrada; si no hay sesión manda al login)
  data/
    remote/ApiService.kt   Endpoints de /api/v1 que usa el cliente (Retrofit)
    remote/RetrofitCliente Singleton con interceptor que agrega el token Bearer
    remote/dto/            Modelos de la API (conservan el snake_case)
    repositorio/           Auth, Catalogo — convierten llamadas en Resultado
    sesion/SesionManager   Token y usuario en SharedPreferences
    Resultado.kt           Exito / Error + llamarApi { }
  ui/
    auth/                  LoginActivity + LoginViewModel
    catalogo/              CatalogoViewModel + ProductoAdapter
  util/Formato.kt          Formato de moneda
```

Patrón: **Activity → ViewModel (StateFlow) → Repositorio → ApiService**. Las pantallas usan ViewBinding.

## Estado

Listos: login (solo cuentas de cliente), cierre de sesión y catálogo con búsqueda.
Pendientes: ficha de producto (`ui/producto`), carrito (`ui/carrito`), checkout con dirección y pago (`ui/checkout`), mis pedidos y calificar (`ui/pedidos`). `ApiService` y los DTO de `CompraDto.kt` ya declaran esos endpoints.

## Reglas

- La app no calcula precios, totales ni existencias: todo viene de la API.
- En release solo se permite HTTPS (`network_security_config.xml`).

## CI

`comprasegura-android.yml` corre las pruebas unitarias y genera el APK debug como artefacto en `main` y `dev`. El APK firmado para evaluadores se genera en Android Studio con *Build > Generate Signed App Bundle / APK*.
