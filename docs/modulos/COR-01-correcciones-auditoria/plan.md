# Plan — COR-01 Correcciones de la auditoría del avance inicial

| Campo | Valor |
|---|---|
| Estado | Cerrado (pruebas aprobadas por el responsable el 2026-10-22) |
| Versión | 1 |
| Fecha | 2026-10-21 |
| Origen | `docs/auditoria/2026-10-21-auditoria-avance-inicial.md` |
| Módulos afectados | R01, R02, R03, R04, R05 y componentes comunes |
| Decisiones del responsable | AUD-O1: citas solo a futuro · AUD-O2: solapamiento según la duración del servicio · AUD-O3: motivo obligatorio |

## 1. Objetivo

Corregir los 15 hallazgos de la auditoría aplicando las decisiones del responsable, sin cambiar el alcance funcional de la Iteración 5 y sin modificar migraciones ya aplicadas.

## 2. Alcance

**Incluye:** las correcciones AUD-01 a AUD-15, la migración nueva `V5`, la actualización de los casos de prueba manuales de R01 a R05 y el ajuste de las pruebas automatizadas de apoyo afectadas por las nuevas reglas.

**No incluye:** módulos nuevos (R06 a R10), edición o baja de clientes y mascotas, administración del catálogo de servicios. Las mejoras opcionales de la sección 7 solo se hacen si el responsable las aprueba.

## 3. Criterios de aceptación

| ID | Hallazgo | Criterio verificable | Mensaje |
|---|---|---|---|
| CA-01 | AUD-01 | *Nueva cantidad* vacía no se guarda y el stock no cambia. | "Cantidad inválida" |
| CA-02 | AUD-02 | Entre las 20:00 y 23:59 el filtro de citas inicia en la fecha local de hoy y la fecha de nacimiento de mañana se rechaza. | "Edad o peso inválido" |
| CA-03 | AUD-03, O1 | No se registra ni se modifica una cita con fecha y hora iguales o anteriores al momento actual; el calendario no ofrece días pasados. | "Seleccione una fecha y hora futuras" |
| CA-04 | AUD-04, O2 | Con una cita programada de 10:00 a 10:30, otra que empiece a las 10:15 (o que termine después de 10:00 empezando antes) se rechaza; una que empiece a las 10:30 se acepta. | "Horario no disponible, seleccione otro" |
| CA-05 | AUD-05 | Cada actualización de stock guarda existencia anterior y resultante; los movimientos previos quedan completados. | — |
| CA-06 | AUD-06 | Con la sesión vencida, cualquier acción lleva al inicio de sesión con un aviso. | "Su sesión expiró, inicie sesión nuevamente" |
| CA-07 | AUD-07 | El selector de categorías conserva todas las categorías después de filtrar; *Actualizar stock* encuentra productos por parte del nombre o código en todo el inventario y, si hay varios, permite elegir uno. | "Producto no encontrado" |
| CA-08 | AUD-08 | El listado de citas se muestra aunque un cliente o servicio relacionado esté inactivo. | — |
| CA-09 | AUD-09 | Dos registros simultáneos en el mismo horario del mismo veterinario no pueden quedar ambos programados. | "Horario no disponible, seleccione otro" |
| CA-10 | AUD-10 | Correo con formato incorrecto, peso con más de 4 enteros o 2 decimales, precio con más de 8 enteros o 2 decimales, o sexo distinto de M/H se rechazan con mensaje de campo. | "Correo inválido" · "Edad o peso inválido" · "Precio o cantidad inválidos" · "Sexo inválido" |
| CA-11 | AUD-11 | JSON mal formado, parámetros con tipo incorrecto y errores inesperados responden con mensaje en español; los errores propios de Spring conservan su código. | "Datos con formato inválido" · "Parámetro inválido" · "Error interno, intente nuevamente" |
| CA-12 | AUD-12 | Una cuenta que no es de veterinario con contraseña incorrecta recibe "Contraseña incorrecta"; solo con contraseña correcta se informa el rol no habilitado. | "Contraseña incorrecta" |
| CA-13 | AUD-13 | El arranque del backend ya no muestra `Using generated security password`. | — |
| CA-14 | AUD-14 | Inventario y citas obtienen el veterinario de la sesión con la misma regla (usuario activo, rol veterinario y registro en `veterinario`). | "Veterinario no autorizado" |
| CA-15 | AUD-15 | `decisiones.md` y `comparacion-interfaces-iteracion5.md` reflejan el alcance real y registran las decisiones O1 a O3. | — |
| CA-16 | O3 | El motivo de la cita sigue siendo obligatorio y la decisión queda documentada. | "Complete los campos obligatorios" |

## 4. Diseño

### 4.1 Datos — migración `V5__completar_movimiento_inventario.sql`

1. Agregar `stock_anterior BIGINT` y `stock_resultante BIGINT` a `movimiento_inventario`.
2. Completar las filas existentes leyendo el motivo `Stock actualizado de X a Y` con funciones de texto estándar (`POSITION`, `SUBSTRING`), compatibles con PostgreSQL y con la base H2 de las pruebas de apoyo.
3. Declarar ambas columnas `NOT NULL` con `CHECK (>= 0)`, como exige el diccionario 4.7.12.

No se usan índices parciales: H2 no los admite. La concurrencia de citas (AUD-09) se resuelve con bloqueo de fila (4.2).

### 4.2 Backend

| Hallazgo | Cambio |
|---|---|
| AUD-03 | `CitaCreateRequest` y `CitaUpdateRequest`: `fechaHora` con `@Future(message = "Seleccione una fecha y hora futuras")`. |
| AUD-04 | `CitaService.validateAvailability` calcula el intervalo `[inicio, inicio + duración)` con la duración del servicio elegido y lo compara con las citas `PROGRAMADA` del veterinario de ese día y del anterior, usando la duración de cada una. Se excluye la propia cita al modificar. |
| AUD-09 | Antes de validar, `CitaService` bloquea la fila del veterinario (`SELECT … FOR UPDATE` en `VeterinarioMapper`) dentro de la transacción; registros simultáneos del mismo veterinario se atienden uno tras otro. |
| AUD-08 | `CitaService.listar` carga clientes, mascotas, servicios y veterinarios de todas las citas en una consulta por tabla (`selectBatchIds`) y no exige estado activo para mostrar. |
| AUD-05 | `InventarioService.actualizarStock` guarda `stockAnterior` y `stockResultante`; `MovimientoInventario` incorpora ambos campos. |
| AUD-07 | Nuevo `GET /api/inventario/categorias`: categorías distintas de los productos activos. La búsqueda existente (`buscar`) ya es parcial y sirve para *Actualizar stock*. |
| AUD-10 | `ClienteCreateRequest.correo`: `@Email(message = "Correo inválido")`. `MascotaCreateRequest.peso`: `@Digits(integer = 4, fraction = 2, message = "Edad o peso inválido")`; `sexo`: `@Pattern(regexp = "M\|H", message = "Sexo inválido")`. `ProductoCreateRequest.precio`: `@Digits(integer = 8, fraction = 2, message = "Precio o cantidad inválidos")`. |
| AUD-11 | `GlobalExceptionHandler`: `HttpMessageNotReadableException` → 400 "Datos con formato inválido"; `MethodArgumentTypeMismatchException` → 400 "Parámetro inválido"; excepciones de Spring con estado propio conservan su código; cualquier otra → 500 "Error interno, intente nuevamente" (registrada en el log). |
| AUD-12 | `AuthService.autenticar`: orden usuario → contraseña → rol. |
| AUD-13 | `HospivetApplication`: excluir `UserDetailsServiceAutoConfiguration`. |
| AUD-14 | Nuevo `VeterinarioSesionService.requireIdVeterinario(nombreUsuario)` en el paquete `veterinario`, usado por `InventarioService` y `CitaService`. |

### 4.3 Frontend

| Hallazgo | Cambio |
|---|---|
| AUD-02 | Nuevo `src/utils/fechas.js` con `hoyLocal()` (AAAA-MM-DD en hora local); se usa en `CitasView.vue` y `MascotasView.vue`. |
| AUD-03 | `CitasView.vue`: el calendario deshabilita días pasados; validación local de fecha y hora futuras con el mismo mensaje del servidor. |
| AUD-01 | `InventarioView.vue`: *Nueva cantidad* vacía o con espacios → "Cantidad inválida". |
| AUD-07 | `InventarioView.vue`: categorías desde `GET /api/inventario/categorias`; *Buscar producto* en stock consulta el servidor con coincidencia parcial: 0 resultados → "Producto no encontrado", 1 → se selecciona, varios → selector para elegir. |
| AUD-06 | `api/http.js`: interceptor de respuestas configurado en `main.js`; ante 401 (salvo en `/auth/login` y `/auth/me`) limpia la sesión y redirige a `/login`, donde se muestra "Su sesión expiró, inicie sesión nuevamente". |
| AUD-10 | `ClientesView.vue`: regla de formato de correo con "Correo inválido". |
| AUD-O4 (edad) | `MascotasView.vue`: menores de un año muestran la edad en meses ("5 meses"). |

### 4.4 Documentación

- `docs/trazabilidad/decisiones.md`: actualizar *Alcance actual* y agregar D-10 (citas solo a futuro), D-11 (disponibilidad por duración del servicio) y D-12 (motivo obligatorio).
- `docs/trazabilidad/comparacion-interfaces-iteracion5.md`: inventario y citas implementados.
- `docs/trazabilidad/requisitos-citas.md` y `requisitos-inventario.md`: nuevas reglas y columnas.
- Planes y pruebas de R01 a R05: marcar las correcciones hechas y actualizar los resultados esperados.

## 5. Decisiones

| ID | Decisión | Motivo |
|---|---|---|
| D-10 | Citas solo con fecha y hora posteriores al momento actual (registrar y modificar). | Decisión del responsable (AUD-O1). |
| D-11 | Un horario está ocupado si se solapa con la duración del servicio de otra cita programada del mismo veterinario. | Decisión del responsable (AUD-O2); el servicio ya registra `duracion_min`. |
| D-12 | El motivo de la cita es obligatorio. | Decisión del responsable (AUD-O3); difiere del diccionario, que lo deja opcional. |
| D-13 | La concurrencia de citas se controla con bloqueo de la fila del veterinario y no con índice parcial. | El índice parcial no cubre solapamientos y H2 no lo admite. |

## 6. Tareas

- [x] T1 Migración V5 y modelo `MovimientoInventario` (AUD-05).
- [x] T2 `VeterinarioSesionService` y su uso en inventario y citas (AUD-14).
- [x] T3 Reglas de citas: futuro, solapamiento, bloqueo y listado robusto (AUD-03, 04, 08, 09).
- [x] T4 Validaciones de DTO (AUD-10) y manejador de errores (AUD-11).
- [x] T5 Orden de verificación del login y exclusión del usuario en memoria (AUD-12, 13).
- [x] T6 Endpoint de categorías y cambios de `InventarioView.vue` (AUD-01, 07).
- [x] T7 `fechas.js`, cambios de `CitasView.vue`, `MascotasView.vue` y `ClientesView.vue` (AUD-02, 03, 10, edad en meses).
- [x] T8 Interceptor de sesión vencida y aviso en el inicio de sesión (AUD-06).
- [x] T9 Ajustar las pruebas automatizadas de apoyo: fechas de citas relativas al día actual y nuevos casos de las reglas cambiadas.
- [x] T10 Documentación de la sección 4.4 y casos de `pruebas.md` de R01 a R05.
- [x] T11 Verificación previa de los casos de `pruebas.md` de este plan en la aplicación en marcha.

## 7. Mejoras opcionales (solo con aprobación expresa)

| ID | Mejora | Propuesta |
|---|---|---|
| OP-01 | AUD-O4: botón "Sí, eliminar producto" en verde. | Usar el estilo rojo de acción destructiva. |
| OP-02 | AUD-O4: no hay forma de ver los clientes y mascotas registrados. | Fuera del alcance de la Iteración 5; se sugiere dejarlo para un plan propio. |
| OP-03 | Tras registrar o modificar una cita en otro día, el listado sigue en la fecha anterior y no la muestra. | Al volver, filtrar por la fecha de la cita guardada. |

## 8. Riesgos y preguntas abiertas

- La verificación previa requiere los servidores en marcha y una cuenta veterinaria de prueba: se necesita que el responsable inicie el backend con su PostgreSQL o autorice crear una base local de prueba.
- Si existen movimientos de inventario con un motivo distinto al generado por el sistema, la migración V5 no podrá completarlos; se revisará antes de aplicarla en la base local.

## 9. Commits previstos (tras la aprobación de las pruebas)

1. `fix(inventario)`: AUD-01, 05, 07, 10 (precio) y V5.
2. `fix(citas)`: AUD-02 (citas), 03, 04, 08, 09.
3. `fix(backend)`: AUD-10 (cliente, mascota), 11, 12, 13, 14.
4. `fix(frontend)`: AUD-02 (mascotas), 06, 10 (correo), edad en meses.
5. `test(backend)`: ajuste de pruebas de apoyo.
6. `docs`: trazabilidad, planes y pruebas actualizados.
