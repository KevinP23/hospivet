# Pruebas manuales — R08 Gestión de atención médica

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, base PostgreSQL local con la migración V6 aplicada, sesión de veterinario, cliente `Ana Pérez` con la mascota `Firulais` (sin atenciones previas) y una cita programada de Firulais (motivo `Control clínico`) para el veterinario de la sesión.

La verificación previa se hizo con una base H2 temporal en memoria y una cuenta veterinaria de prueba creada solo para esa sesión; no se usó la base PostgreSQL del responsable.

Cada caso corresponde a un criterio del plan v2, que sale de la plantilla y del diagrama de secuencia del caso de uso. Los casos CP-R08-09 a CP-R08-12 verifican las decisiones N-01 a N-06 (opción A).

## Resumen

| Caso | Criterio | Caso de uso / secuencia | Verificación previa | Revisión del responsable |
|---|---|---|---|---|
| CP-R08-01 | CA-02 | 3.1.22 curso 2c / SD-22 | Correcto | Aprobado |
| CP-R08-02 | CA-03 | 3.1.23 curso 4b / SD-23 | Correcto | Aprobado |
| CP-R08-03 | CA-04 | 3.1.23 pasos 4a–6 / SD-23 | Correcto | Aprobado |
| CP-R08-04 | CA-05 | 3.1.24 curso 4b / SD-24 | Correcto | Aprobado |
| CP-R08-05 | CA-06 | 3.1.24 pasos 4a–6 / SD-24 | Correcto | Aprobado |
| CP-R08-06 | CA-07 | 3.1.25 curso 4b / SD-25 | Correcto | Aprobado |
| CP-R08-07 | CA-08 | 3.1.25 pasos 4a–5 / SD-25 | Correcto | Aprobado |
| CP-R08-08 | CA-01 | 3.1.22 pasos 1–2b / SD-22 | Correcto | Aprobado |
| CP-R08-09 | N-01, N-02, N-06 | 3.1.23 / SD-23 | Correcto | Aprobado |
| CP-R08-10 | N-03 | 3.1.25 curso 4b / SD-25 | Correcto | Aprobado |
| CP-R08-11 | N-04 | 3.1.24 / diccionario 4.7.9 | Correcto | Aprobado |
| CP-R08-12 | N-05 | Interfaz Consultar historial | Correcto | Aprobado |

## Casos

### CP-R08-01 · Sin historial previo
- **Requisito / criterio:** R8.1.3 / CA-02
- **Pasos:** 1. *Atención médica* → *Consultar historial*. 2. Buscar mascota `Firulais — Ana Pérez`. 3. *Buscar historial*.
- **Resultado esperado:** "Sin historial previo"; el criterio de búsqueda se conserva; no se muestran filas.
- **Verificación previa:** 2026-10-23 · Correcto · "Sin historial previo" a todo el ancho; se conserva la mascota elegida.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R08-02 · Diagnóstico vacío
- **Requisito / criterio:** R8.2.2–3 / CA-03
- **Pasos:** 1. *Registrar diagnóstico*: mascota Firulais y su atención médica. 2. Dejar *Diagnóstico* vacío. 3. *Guardar diagnóstico*.
- **Resultado esperado:** alerta y mensaje de campo "Ingrese un diagnóstico"; mascota y atención se conservan; no se guarda.
- **Verificación previa:** 2026-10-23 · Correcto · alerta y mensaje de campo "Ingrese un diagnóstico"; mascota y atención se conservaron.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R08-03 · Registrar diagnóstico
- **Requisito / criterio:** R8.2.4–5 / CA-04
- **Pasos:** 1. Diagnóstico `Otitis leve`. 2. *Guardar diagnóstico*.
- **Resultado esperado:** "Diagnóstico guardado".
- **Verificación previa:** 2026-10-23 · Correcto · "Diagnóstico guardado".
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R08-04 · Tratamiento vacío
- **Requisito / criterio:** R8.3.2–3 / CA-05
- **Pasos:** 1. *Registrar tratamiento*: diagnóstico asociado `D-… — Otitis leve`. 2. Dejar *Tratamiento indicado* vacío. 3. *Guardar tratamiento*.
- **Resultado esperado:** "Ingrese un tratamiento"; no se guarda y se conservan los datos.
- **Verificación previa:** 2026-10-23 · Correcto · "Ingrese un tratamiento".
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R08-05 · Registrar tratamiento
- **Requisito / criterio:** R8.3.4–5 / CA-06
- **Pasos:** 1. Tratamiento indicado `Gotas óticas 7 días`, duración `7 días`, fechas de inicio y fin. 2. *Guardar tratamiento*.
- **Resultado esperado:** "Tratamiento guardado".
- **Verificación previa:** 2026-10-23 · Correcto · "Tratamiento guardado".
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R08-06 · Receta sin medicamento o dosis
- **Requisito / criterio:** R8.4.2–3 / CA-07
- **Pasos:** 1. *Registrar receta*: atención y mascota `AT-… — Firulais`. 2. Llenar frecuencia `Cada 12 horas`, duración e indicaciones, y dejar medicamento y dosis vacíos. 3. *Guardar receta*.
- **Resultado esperado:** alerta "Complete medicamento y dosis" y el mismo mensaje bajo *Medicamento* y *Dosis*; no se guarda y se conservan los datos.
- **Verificación previa:** 2026-10-23 · Correcto · alerta y mensaje bajo *Medicamento* y *Dosis*: "Complete medicamento y dosis".
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R08-07 · Registrar receta
- **Requisito / criterio:** R8.4.4 / CA-08
- **Pasos:** 1. Medicamento `Gotas óticas`, dosis `3 gotas`. 2. *Guardar receta*.
- **Resultado esperado:** "Receta guardada".
- **Verificación previa:** 2026-10-23 · Correcto · "Receta guardada".
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R08-08 · Historial clínico de la mascota
- **Requisito / criterio:** R8.1.1–2 / CA-01
- **Pasos:** 1. *Consultar historial* de Firulais.
- **Resultado esperado:** se muestra el historial con la fecha de la atención, el diagnóstico `Otitis leve`, el tratamiento `Gotas óticas 7 días` y la receta `Gotas óticas`.
- **Verificación previa:** 2026-10-23 · Correcto · fila 23/10/2026 · Control clínico · Otitis leve · Gotas óticas 7 días · Gotas óticas.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R08-09 · La atención nace de la cita
- **Decisiones:** N-01, N-02, N-06 (opción A)
- **Pasos:** 1. *Registrar diagnóstico*: mascota Firulais; abrir *Atención médica*. 2. Elegir la cita programada `CITA-… — fecha`, diagnóstico `Otitis leve`, *Guardar diagnóstico*. 3. Volver a abrir *Atención médica*. 4. Ir a *Citas* y filtrar el día de la cita. 5. *Consultar historial* de Firulais.
- **Resultado esperado:** 1 → la lista muestra la cita programada. 2 → "Diagnóstico guardado". 3 → aparece la atención `AT-… — fecha de hoy` y la cita ya no figura. 4 → la cita está *Atendida*. 5 → la columna *Atención* muestra `Control clínico`.
- **Verificación previa:** 2026-10-23 · Correcto · el selector mostró `CITA-00001 — 24/10/2026 10:00`; tras guardar mostró `AT-00001 — 23/10/2026` y la cita quedó `ATENDIDA`; el historial muestra "Control clínico".
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R08-10 · Frecuencia vacía
- **Decisión:** N-03 (opción A)
- **Pasos:** 1. *Registrar receta*: medicamento `Gotas óticas`, dosis `3 gotas`, frecuencia vacía. 2. *Guardar receta*.
- **Resultado esperado:** "Complete medicamento y dosis" en la alerta y bajo *Frecuencia*; no se guarda.
- **Verificación previa:** 2026-10-23 · Correcto · frecuencia vacía → "Complete medicamento y dosis" bajo *Frecuencia*.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R08-11 · Fecha de fin
- **Decisión:** N-04 (opción A)
- **Pasos:** 1. *Registrar tratamiento*: fecha de inicio 10/11/2026. 2. Abrir el calendario de *Fecha de fin*.
- **Resultado esperado:** los días anteriores al 10/11/2026 aparecen deshabilitados.
- **Verificación previa:** 2026-10-23 · Correcto · con inicio 10/11/2026, el calendario de fin deshabilita los días anteriores.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R08-12 · Volver a la ficha
- **Decisión:** N-05 (opción A)
- **Pasos:** 1. En *Consultar historial*, presionar *Volver a la ficha*.
- **Resultado esperado:** se abre *Registrar mascota*.
- **Verificación previa:** 2026-10-23 · Correcto · abre *Registrar mascota*.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**
