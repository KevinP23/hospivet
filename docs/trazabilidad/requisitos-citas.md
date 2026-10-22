# Trazabilidad del módulo de citas

Fuente principal: Iteración 5 integrada, casos R5.1 a R5.4. El modelo de datos se contrasta con la Iteración 4 corregida. Los documentos Word originales no se modifican.

## Requisitos implementados

| Requisito | Implementación |
|---|---|
| R5.1, R5.1.1–5 | Registro de cita con cliente, mascota vinculada, veterinario autenticado, servicio, fecha, hora y motivo. |
| R5.2, R5.2.1–5 | Modificación de fecha, hora y servicio de una cita programada. |
| R5.3, R5.3.1–4 | Confirmación y cambio de estado a `CANCELADA`; la cita no se elimina. |
| R5.4, R5.4.1–7 | Listado del veterinario autenticado con fecha, hora, cliente, mascota, servicio, estado y filtro por día. |

## Validaciones y flujos alternativos

- La mascota seleccionada debe estar activa y pertenecer al cliente seleccionado.
- El servicio debe estar activo.
- Una cita nueva queda en estado `PROGRAMADA`.
- Una cita ocupa desde su fecha y hora de inicio hasta el fin de la duración de su servicio. No se permite que dos citas `PROGRAMADA` del mismo veterinario se solapen (D-11).
- La fecha y hora deben ser posteriores al momento actual al registrar y al modificar: `Seleccione una fecha y hora futuras` (D-10).
- El motivo es obligatorio (D-12).
- El registro y la modificación bloquean la agenda del veterinario durante la transacción para evitar reservas simultáneas (D-13).
- El conflicto conserva los datos del formulario y muestra exactamente: `Horario no disponible, seleccione otro`.
- Si el filtro no devuelve filas se conserva la fecha y se muestra: `No hay citas registradas para la fecha seleccionada`.
- Modificar y cancelar solo operan sobre citas programadas del veterinario que inició sesión.

## Relaciones persistidas

- `cliente` 1:N `cita`.
- `mascota` 1:N `cita`.
- `veterinario` 1:N `cita`.
- `servicio` 1:N `cita`.

## Servicio inicial aprobado

Se incorpora `Consulta general`, que es el servicio visible en el formulario documentado. La Iteración 5 no contiene un caso de uso para administrar el catálogo de servicios.

Para satisfacer los campos obligatorios del modelo de Iteración 4 se utilizan temporalmente estos valores técnicos: descripción `Consulta veterinaria general`, precio `0.00 Bs` y duración `30 minutos`. Deben confirmarse antes de usar precio o duración para facturación, agenda por intervalos o reportes.

## Correspondencia técnica

- Migración: `V3__crear_servicios_y_citas.sql`.
- API: `/api/servicios` y `/api/citas`.
- Interfaz: ruta `/citas`.
- La disponibilidad se valida en el servicio de aplicación dentro de una transacción.
- Los códigos visibles `CITA-00001` son derivados del identificador y no agregan una columna distinta al modelo.
