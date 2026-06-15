# MiniMarket Plus — Pruebas unitarias (Desarrollo Backend II, PBY2202)

Configuración del entorno de pruebas unitarias y diseño de pruebas con
**JUnit 5 + Mockito + JaCoCo** sobre el backend de "MiniMarket Plus"
(Spring Boot 3, Java 17). Corresponde a la actividad de la **Semana 4** del ramo
*Desarrollo Backend II* (PBY2202, Duoc UC).

## Herramientas de testing

| Herramienta | Uso |
|---|---|
| **JUnit 5 (Jupiter)** | Framework de pruebas (`@Test`, `@BeforeEach`, `@DisplayName`, aserciones). |
| **Mockito** | Simulación de dependencias (`@Mock`, `@InjectMocks`, `when/thenReturn`, `verify`). |
| **JaCoCo** | Medición de cobertura de código (reporte en `target/site/jacoco/index.html`). |

## Lógica de negocio probada

Se añadió lógica de negocio testeable en los servicios:

- **`UsuarioService`** — `datosCompletos()` (valida nombre, apellido, email y
  dirección), `registrarUsuario()` (rechaza datos incompletos) y `tieneRol()`.
- **`VentaService`** — `calcularTotal()` (suma precio × cantidad de los
  detalles), `hayStockSuficiente()` (consulta el repositorio de productos) y
  `registrarVenta()` (valida usuario y stock).

## Pruebas implementadas (26 en total)

- **`UsuarioServiceTest`** (11): datos completos/incompletos, registro con
  validación, verificación de rol y delegación CRUD. Mock de `UsuarioRepository`.
- **`VentaServiceTest`** (11): cálculo de total, validación de stock, registro
  válido, fallos por stock y por usuario inválido, y relaciones
  Venta–Usuario / Venta–Producto. Mocks de `ProductoRepository`,
  `VentaRepository` y `UsuarioService`.
- **`UsuarioTest`** (3): pruebas de la entidad (incluidas en el proyecto base).

## Cobertura JaCoCo

| Clase | Antes | Después |
|---|---|---|
| `UsuarioServiceImpl` | 10 % | **96 %** |
| `VentaServiceImpl` | 11 % | **96 %** |

Las capturas del reporte (antes y después), la corrida de pruebas en terminal y
el código de las pruebas están en la carpeta [`evidencias/`](evidencias/).

## Ejecución

```bash
# Ejecutar las pruebas y generar el reporte de cobertura
./mvnw clean test

# El reporte HTML de JaCoCo queda en:
#   target/site/jacoco/index.html
```
