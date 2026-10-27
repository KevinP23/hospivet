# Revisiones — R07 Gestión de facturación

Registro de decisiones del responsable del proyecto. Cada fila es una revisión de plan o de pruebas.

| Fecha | Versión del plan | Fase revisada | Decisión | Observaciones del responsable | Respuesta / acción |
|---|---|---|---|---|---|
| 2026-10-27 | 1 | Plan | Decisiones parciales | Elige la opción A en N-01 a N-11 e indica que el sistema se instalará en la computadora de la veterinaria, no como sitio web. | Plan v2: se registra el contexto de instalación local (D-16). Pendientes: precio real de *Consulta general* (N-03) y confirmar N-08, para el que se recomienda la opción B por la instalación local. |
| 2026-10-27 | 2 | Plan | Decisión | Cambia N-08 a la opción B: impresión directa en la impresora del equipo. | Plan v2 actualizado. Pendiente el precio real de *Consulta general* (N-03) para aprobar el plan. |
| 2026-10-27 | 2 | Plan | Aprobado | N-03: "por el momento es ficticio todo, luego ya se cambiará por el precio real". | Precio de prueba de *Consulta general*: Bs 80,00 (mockup de *Generar factura*). Plan v2 en estado En codificación. |
| 2026-10-27 | 2 | Pruebas | Pendiente | — | Codificación terminada y verificación previa registrada en `pruebas.md`: 16 casos correctos y CP-R07-11 pendiente de configurar el servidor de correo. Queda a la espera de la revisión manual del responsable. |
| 2026-10-27 | 2 | Pruebas | Aprobado | Revisó manualmente R07 y aprueba las pruebas; el envío por correo funciona con Gmail configurado en el equipo. | Los 17 casos quedan aprobados y el módulo cerrado. Evidencia de defensa generada y commits C-026 a C-029 agregados al manual Git. |
