# Revisiones — R06 Gestión de reportes

Registro de decisiones del responsable del proyecto. Cada fila es una revisión de plan o de pruebas.

| Fecha | Versión del plan | Fase revisada | Decisión | Observaciones del responsable | Respuesta / acción |
|---|---|---|---|---|---|
| 2026-11-24 | 1 | Plan | Pendiente | — | Plan rehecho desde `Iteracion5_Hospivet.pdf` porque el anterior no se subió al repositorio. Espera la decisión del responsable sobre N-01 a N-08. |
| 2026-11-24 | 1 | Plan | Cambios solicitados | Por indicación del docente, las pantallas deben quedar tal cual el PDF y no se puede agregar nada. | Se agregó la regla al plan. Nueva propuesta sin agregados en pantalla: N-01 B, N-02 A, N-03 A, N-04 A, N-05 B, N-06 C (nueva: rango que no puede quedar inválido, sin mensajes), N-07 A, N-08 A. Encabezados de tabla visibles desde el inicio, como en los mockups. Se ajustaron CP-R06-06 y CP-R06-09. Espera la aprobación. |
| 2026-11-24 | 1 | Plan | Aprobado | Aprueba el plan con la propuesta sin agregados en pantalla. | Opciones registradas: N-01 B, N-02 A, N-03 A, N-04 A, N-05 B, N-06 C, N-07 A, N-08 A (decisión D-19). Pasa a codificación. |
| 2026-11-24 | 1 | Pruebas | Pendiente | — | Codificación terminada y verificación previa registrada en `pruebas.md`: 11 casos correctos. 10 pruebas automáticas de apoyo del backend pasan. Queda a la espera de la revisión manual del responsable. |
| 2026-11-24 | 1 | Pruebas | Aprobado | Revisó manualmente los cuatro reportes y aprueba las pruebas. | Los 11 casos quedan aprobados y el módulo cerrado. Commits propuestos para que el responsable los ejecute: `feat(backend): gestión de reportes`, `feat(frontend): interfaces de reportes`, `test(backend): pruebas de apoyo de reportes` y `docs: plan, pruebas y trazabilidad de R06`. |
| 2026-11-24 | 1 | Pruebas | Observación adicional | Pide los calendarios en español (salían en inglés: "November", "Su Mo Tu"). | Se configuró el idioma español de Element Plus en `frontend/src/main.js`; afecta a todos los calendarios de la aplicación (Citas, Facturación y Reportes), que ahora muestran "noviembre" y "dom lun mar mié jue vie sáb". Verificado en *Reporte de ingresos*. Entra en el commit `feat(frontend): interfaces de reportes`. |
