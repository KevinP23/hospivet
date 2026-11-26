# Revisiones — R01 Autenticación y acceso

Registro de decisiones del responsable del proyecto. Cada fila es una revisión de plan o de pruebas.

| Fecha | Versión del plan | Fase revisada | Decisión | Observaciones del responsable | Respuesta / acción |
|---|---|---|---|---|---|
| 2026-10-21 | 1 | Plan retroactivo | Pendiente | — | Plan creado a partir del código existente y de la auditoría AUD-2026-10-21. |
| 2026-10-21 | 1 | Correcciones propuestas (sección 6) | Aprobadas | Decisiones: AUD-O1 citas solo a futuro; AUD-O2 solapamiento según la duración del servicio; AUD-O3 motivo obligatorio. | Se ejecutan mediante el plan COR-01. La revisión manual del plan retroactivo sigue pendiente. |
| 2026-11-26 | 1 (retroactivo) | Pruebas | Pendiente | — | Verificación previa de los 6 casos: todos funcionan. Al comparar con el mockup del PDF (págs. 52–53) se encontraron diferencias visuales, que se presentan al responsable por la indicación del docente (pantallas tal cual el PDF): D1 alerta arriba de los campos (en el PDF va bajo *Contraseña*); D2 asteriscos rojos en *Usuario* y *Contraseña*; D3 textos "Ingrese el usuario"/"Ingrese la contraseña"; D4 la cabecera dice "Acceso veterinario" (el PDF muestra un nombre; además, desde R10 también entran clientes); D5 aviso "Su sesión expiró, inicie sesión nuevamente" (aprobado en COR-01). |
| 2026-11-26 | 1 (retroactivo) | Pruebas | Observado (respuesta) | Aprueba corregir D1 a D4 y mantener D5. | Corregido en COR-03 (decisión D-22): alerta bajo *Contraseña*, sin asteriscos ni textos de campo vacío y sin "Acceso veterinario". CA-01 y CP-R01-01 actualizados. Los casos de R01 quedan listos para la revisión manual del responsable. |
| 2026-11-26 | 1 (retroactivo) | Pruebas | Aprobado | Revisó manualmente el inicio de sesión (con las correcciones de COR-03) y aprueba las pruebas. | Los 6 casos quedan aprobados y R1.1 cerrado. R1.2 tiene su propio plan, ya cerrado (`R01-recuperar-contrasena`). |
