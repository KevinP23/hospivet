# Revisiones — R1.2 Recuperar contraseña

Registro de decisiones del responsable del proyecto. Cada fila es una revisión de plan o de pruebas.

| Fecha | Versión del plan | Fase revisada | Decisión | Observaciones del responsable | Respuesta / acción |
|---|---|---|---|---|---|
| 2026-11-24 | 1 | Plan | Pendiente | — | Plan redactado desde `Iteracion5_Hospivet.pdf` (3.1.2, interfaz y SD-02) con la regla de no agregar nada a las pantallas. Espera la decisión sobre N-01 a N-05 y el correo del veterinario para las pruebas. |
| 2026-11-24 | 1 | Plan | Aprobado | Aprueba el plan con la opción A en N-01 a N-05. | Opciones registradas (decisión D-20). Pasa a codificación. Falta el correo del veterinario para la verificación previa (T4). |
| 2026-11-25 | 1 | Pruebas | Pendiente | — | Codificación terminada. 11 pruebas automáticas pasan. Verificación previa correcta en CP-R12-01, 02, 06, 07 y 08. CP-R12-03 a 05 requieren que el responsable reciba el correo real, porque cambian la contraseña de `kpinto`. Correo del veterinario registrado: kevinpintocarrasco17@gmail.com. Se corrigió además la prueba existente `rechazaCredencialesIncorrectasSinCrearSesionAutenticada`, que dependía del orden de ejecución. |
| 2026-11-25 | 1 | Pruebas | Aprobado | Probó la recuperación con su correo real: llegó la contraseña temporal y pudo iniciar sesión con ella. Todo funcionó. Preguntó hasta cuándo vale la contraseña temporal. | Los 8 casos quedan aprobados y el módulo cerrado. Respuesta: según N-02 (opción A), la contraseña temporal no vence y queda como contraseña de la cuenta hasta una nueva recuperación; cambiarla por otra elegida requeriría un caso de uso que no está en la Iteración 5. Commits propuestos: `feat(backend): recuperación de contraseña`, `feat(frontend): interfaz de recuperar contraseña`, `test(backend): pruebas de apoyo de recuperar contraseña` y `docs: plan, pruebas y trazabilidad de R1.2`. |
