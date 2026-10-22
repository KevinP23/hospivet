# Plan — R05 Gestión de citas

| Campo | Valor |
|---|---|
| Estado | Implementado (retroactivo) · En revisión |
| Versión | 1 |
| Fecha | 2026-10-21 |
| Requisitos | R5.1–R5.4 y sus subfunciones |
| Casos de uso y secuencias | 3.1.9 Registrar cita — SD-09 · 3.1.10 Modificar cita — SD-10 · 3.1.11 Cancelar cita — SD-11 · 3.1.12 Consultar citas del veterinario — SD-12 |
| Depende de | R02 (cliente), R03 (mascota), catálogo `servicio` |

## 1. Objetivo

Organizar la agenda del veterinario: registrar, modificar, cancelar y consultar citas sin cruces de horario.

## 2. Alcance

**Incluye:** registro con cliente, mascota del cliente, servicio, fecha, hora, motivo y observaciones; modificación de servicio, fecha y hora; cancelación por estado con confirmación; listado del veterinario de la sesión con filtro por día; servicio inicial *Consulta general*.

**No incluye:** marcar citas como `ATENDIDA` (llega con R08), administración del catálogo de servicios, solicitud de citas por el cliente (R10.1).

## 3. Criterios de aceptación

| ID | Requisito | Criterio verificable | Mensaje exacto |
|---|---|---|---|
| CA-01 | R5.1.1 | Al elegir el cliente, el selector de mascota muestra solo sus mascotas. | — |
| CA-02 | R5.1.2 | Sin cliente, mascota, servicio, fecha, hora o motivo no se guarda. | "Complete los campos obligatorios" |
| CA-03 | R5.1.3–4 | Si el horario se solapa con la duración del servicio de otra cita programada del veterinario, no se guarda y se conservan los datos (D-11). | "Horario no disponible, seleccione otro" |
| CA-11 | — | No se registra ni modifica una cita con fecha y hora pasadas; el calendario no ofrece días anteriores (D-10). | "Seleccione una fecha y hora futuras" |
| CA-04 | R5.1.5 | Con datos válidos se guarda en estado *Programada*. | "Cita registrada correctamente" |
| CA-05 | R5.2.1–5 | Modificar cambia servicio, fecha y hora de una cita programada, validando la disponibilidad. | "Cambios de la cita guardados" / "Horario no disponible, seleccione otro" |
| CA-06 | R5.3.1–2, R5.3.4 | Cancelar muestra la cita, exige confirmar y la deja en estado *Cancelada* sin borrarla. | "Cita cancelada correctamente" |
| CA-07 | R5.3.3 | Volver sin confirmar mantiene la cita programada. | — |
| CA-08 | R5.4.1–6 | El listado muestra fecha, hora, cliente, mascota, servicio y estado, y filtra por día. | — |
| CA-09 | R5.4.7 | Un día sin citas lo informa y conserva la fecha elegida. | "No hay citas registradas para la fecha seleccionada" |
| CA-10 | — | Un horario de una cita cancelada vuelve a quedar disponible. | — |

## 4. Diseño implementado

### 4.1 Datos
`servicio` y `cita` (V3); `Consulta general` con precio `0.00 Bs` y duración 30 min provisionales. Estados `PROGRAMADA`, `ATENDIDA`, `CANCELADA`.

### 4.2 Backend
| Método | Ruta | Descripción | Errores |
|---|---|---|---|
| GET | `/api/servicios` | Servicios activos | 401/403 |
| GET | `/api/citas?fecha=` | Citas del veterinario de la sesión | 401/403 |
| POST | `/api/citas` | Registra una cita | 400, 404, 409 horario |
| PUT | `/api/citas/{id}` | Modifica servicio, fecha y hora | 400, 404, 409 |
| PATCH | `/api/citas/{id}/cancelacion` | Cambia el estado a `CANCELADA` | 404, 409 |

Clases: `CitaController`, `CitaService`, `CitaCreateRequest`, `CitaUpdateRequest`, `CitaResponse`, `Cita`, `CitaMapper`, `ServicioController`, `ServicioService`.

### 4.3 Frontend
`CitasView.vue` (ruta `/citas`) con cuatro estados: listado, registrar, modificar y confirmación de cancelación. El veterinario sale de la sesión y no se elige.

## 5. Decisiones y diferencias con la documentación

| ID | Decisión | Motivo |
|---|---|---|
| — | Código visible `CITA-00001` derivado del identificador. | Sin columna adicional en el modelo. |
| — | Precio y duración de *Consulta general* provisionales. | La documentación solo muestra el nombre. |
| D-10 | Citas solo a futuro. | Decisión del responsable (AUD-O1). |
| D-11 | Disponibilidad por solapamiento según la duración del servicio. | Decisión del responsable (AUD-O2). |
| D-12 | Motivo obligatorio. | Decisión del responsable (AUD-O3); difiere del diccionario. |
| D-13 | Bloqueo de la agenda del veterinario al guardar. | Evitar reservas simultáneas. |

## 6. Correcciones (aprobadas el 2026-10-21 · codificadas en COR-01 · pruebas de COR-01 aprobadas el 2026-10-22)

- [x] AUD-02: filtro inicial con la fecha local (hoy usa UTC y después de las 20:00 muestra el día siguiente).
- [x] AUD-03 / AUD-O1: rechazar citas en fecha y hora pasadas (D-10).
- [x] AUD-04 / AUD-O2: validar solapamiento según la duración del servicio (D-11).
- [x] AUD-08: el listado no debe fallar si un cliente o servicio se desactiva; reducir consultas por cita.
- [x] AUD-09: bloqueo de la agenda del veterinario al guardar para impedir reservas simultáneas (D-13).
- [x] AUD-14: unificar la obtención del veterinario de la sesión.
- [x] AUD-O3: motivo obligatorio (D-12).

## 7. Riesgos y preguntas abiertas

- Confirmar precio y duración reales de *Consulta general* antes de facturación (R07) y reportes (R06).
- Tras registrar una cita en otro día, el listado vuelve filtrado por la fecha anterior y no la muestra.
