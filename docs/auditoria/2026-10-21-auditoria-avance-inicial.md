# Auditoría técnica del avance inicial

| Campo | Valor |
|---|---|
| Fecha | 2026-10-21 |
| Commit auditado | `1dd853a` (rama `main`) |
| Alcance | Backend completo, frontend completo, migraciones V1–V4 y documentos de `docs/trazabilidad` |
| Referencia | Iteración 5 integrada (requisitos, plantillas, diccionario 4.7) |

## 1. Resultado general

El avance es **funcional y coherente con la Iteración 5** en los módulos R1.1, R2.1, R3.1, R4.1–R4.4 y R5.1–R5.4. La arquitectura es ordenada por módulo (controlador, servicio, DTO, modelo, mapper), los mensajes visibles coinciden con los documentos y las validaciones existen tanto en la interfaz como en el servidor.

Se encontraron **1 defecto de severidad alta**, **6 de severidad media**, **8 de severidad baja** y **4 observaciones** que requieren decisión del responsable. Ninguna corrección se aplicó: cada una se incorpora como propuesta en el plan del módulo correspondiente y espera aprobación.

## 2. Verificaciones ejecutadas

| Verificación | Resultado |
|---|---|
| Compilación y pruebas existentes del backend (`mvnw test`, H2 en memoria) | 5 de 5 correctas |
| Compilación del frontend (`npm run build`) | Correcta; aviso de paquete JavaScript mayor a 500 kB |
| Migraciones Flyway V1–V4 sobre base vacía | Aplicadas sin errores |
| Revisión de código línea por línea de backend y frontend | Completa |
| Prueba de la interfaz en ejecución | **No realizada**: servidores detenidos y sin credenciales de base local. Queda para la revisión manual de cada módulo. |

## 3. Hallazgos

Severidad: **Alta** (pierde o corrompe datos), **Media** (incumple documentación o permite datos incorrectos), **Baja** (robustez, mantenimiento o caso poco frecuente), **Obs.** (requiere decisión).

| ID | Sev. | Módulo | Hallazgo | Ubicación | Propuesta |
|---|---|---|---|---|---|
| AUD-01 | Alta | R04 | En *Actualizar stock*, si el campo *Nueva cantidad* queda vacío, `Number('')` vale `0` y el stock se guarda en cero en lugar de mostrar `Cantidad inválida`. | `frontend/src/views/inventario/InventarioView.vue:160` | Rechazar valor vacío antes de convertirlo a número. |
| AUD-02 | Media | R03, R05 | La fecha de “hoy” se calcula en UTC (`toISOString`). En Bolivia (UTC−4), entre las 20:00 y 23:59 el filtro de citas inicia en el día siguiente y la validación local de fecha de nacimiento acepta el día de mañana. | `CitasView.vue:23`, `MascotasView.vue:14` | Calcular la fecha con la zona horaria local. |
| AUD-03 | Media | R05 | Se puede registrar o modificar una cita con fecha y hora ya pasadas. | `CitaService.registrar/modificar`, `CitasView.vue` | Exigir fecha y hora futuras (requiere decisión, ver AUD-O1). |
| AUD-04 | Media | R05 | La disponibilidad solo compara la hora exacta; ignora la duración del servicio (30 min). Una cita a las 10:00 y otra a las 10:15 del mismo veterinario se aceptan. | `CitaService.java:106` | Validar solapamiento por duración (requiere decisión, ver AUD-O2). |
| AUD-05 | Media | R04 | `movimiento_inventario` no tiene `stock_anterior` ni `stock_resultante`, columnas obligatorias del diccionario 4.7.12. Hoy esos valores solo aparecen dentro del texto del motivo. | `V2__crear_inventario.sql`, `InventarioService.actualizarStock` | Nueva migración V5 que agregue ambas columnas y guardarlas en cada movimiento. |
| AUD-06 | Media | Todos | Si la sesión expira, las pantallas muestran un error genérico y no regresan al inicio de sesión. | `frontend/src/api/http.js` | Interceptor que, ante respuesta 401, limpie la sesión y redirija a `/login`. |
| AUD-07 | Baja | R04 | El selector de categorías se arma con la lista ya filtrada: tras filtrar, solo queda la categoría elegida. Además, la búsqueda de *Actualizar stock* solo busca en la lista filtrada y exige coincidencia exacta. | `InventarioView.vue:28`, `InventarioView.vue:140` | Cargar categorías de todo el inventario y buscar sobre el inventario completo con coincidencia parcial. |
| AUD-08 | Baja | R05 | Si un cliente o servicio relacionado se desactiva, el listado completo de citas falla (usa validaciones de “activo” al construir la respuesta). Además hace 4 consultas por cita. | `CitaService.java:148` | Leer cliente y servicio sin exigir estado activo al mostrar; consulta con uniones. |
| AUD-09 | Baja | R05 | Dos solicitudes simultáneas pueden reservar el mismo horario (validación y registro no están protegidos por un índice único). | `V3__crear_servicios_y_citas.sql` | Índice único parcial `(id_veterinario, fecha_hora) WHERE estado = 'PROGRAMADA'` en una nueva migración. |
| AUD-10 | Baja | R02, R03, R04 | Faltan validaciones: formato de correo del cliente; límite de dígitos de peso (6,2) y precio (10,2), que hoy terminan en el mensaje genérico “El registro incumple una restricción de datos”; el sexo de la mascota acepta cualquier carácter por API. | DTO de cliente, mascota y producto | `@Email`, `@Digits` y patrón `M|H`. |
| AUD-11 | Baja | Común | El manejador de errores no cubre JSON mal formado, parámetros con tipo incorrecto ni errores inesperados; esas respuestas llegan sin `message` y la interfaz muestra un texto genérico. | `GlobalExceptionHandler.java` | Manejadores para esos casos con mensaje en español. |
| AUD-12 | Baja | R01 | El inicio de sesión informa que la cuenta no es de veterinario antes de verificar la contraseña. | `AuthService.java:36-40` | Verificar contraseña primero y luego el rol. |
| AUD-13 | Baja | Común | Spring crea un usuario en memoria con contraseña aleatoria (`Using generated security password`). No es utilizable, pero es configuración sobrante. | Arranque del backend | Excluir `UserDetailsServiceAutoConfiguration`. |
| AUD-14 | Baja | R04, R05 | La búsqueda del veterinario autenticado está duplicada en dos servicios con reglas diferentes (solo citas verifica la tabla `veterinario`). | `InventarioService.java:110`, `CitaService.java:137` | Un único servicio compartido. |
| AUD-15 | Media | Docs | `decisiones.md` (Alcance actual) y `comparacion-interfaces-iteracion5.md` siguen diciendo que inventario y citas están fuera de alcance o deshabilitados. | `docs/trazabilidad/decisiones.md:25`, `comparacion-interfaces-iteracion5.md:38` | Actualizar ambos textos. |

## 4. Observaciones que requieren decisión del responsable

| ID | Tema | Opciones |
|---|---|---|
| AUD-O1 | ¿Las citas deben ser siempre a futuro? | a) Sí, rechazar fechas pasadas · b) Permitirlas para registrar citas atrasadas |
| AUD-O2 | ¿Qué significa “horario ocupado”? | a) Misma hora exacta (actual) · b) Cualquier solapamiento según la duración del servicio |
| AUD-O3 | El motivo de la cita es obligatorio en la implementación, pero el diccionario lo deja opcional y R5.1.2 no lo menciona. | a) Mantener obligatorio · b) Hacerlo opcional |
| AUD-O4 | Experiencia de uso: no hay listado para comprobar clientes y mascotas registrados; la edad muestra “0 años” para mascotas menores de un año; el botón “Sí, eliminar producto” usa el color principal verde en una acción destructiva. | Revisar en la prueba manual y decidir |

### Resolución del responsable (2026-10-21)

| ID | Decisión |
|---|---|
| AUD-O1 | Citas solo a futuro. |
| AUD-O2 | Validar solapamiento según la duración del servicio. |
| AUD-O3 | Motivo obligatorio. |
| AUD-O4 | Edad en meses incluida en las correcciones; color del botón de eliminación y listado de clientes y mascotas quedan como mejoras opcionales OP-01 y OP-02. |

## 5. Siguiente paso

Las correcciones propuestas en los planes de R01 a R05 fueron aprobadas y se ejecutan mediante el plan [`COR-01`](../modulos/COR-01-correcciones-auditoria/plan.md), que se codifica después de su aprobación.
