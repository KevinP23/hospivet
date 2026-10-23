# Pruebas manuales — R09 Gestión de promociones

Entorno: backend en `http://localhost:8080`, frontend en `http://localhost:5173/`, base PostgreSQL local con la migración V7 aplicada, sesión de veterinario, servicio *Consulta general* y productos de inventario registrados, módulo *Promociones*.

La verificación previa se hizo con una base H2 temporal en memoria y una cuenta veterinaria de prueba creada solo para esa sesión; no se usó la base PostgreSQL del responsable.

Cada caso corresponde a un criterio del plan, que sale de la plantilla, la interfaz y el diagrama de secuencia del caso de uso. Los casos CP-R09-11 a CP-R09-14 verifican las decisiones N-01 a N-06 (opción A).

## Resumen

| Caso | Criterio | Caso de uso / secuencia | Verificación previa | Revisión del responsable |
|---|---|---|---|---|
| CP-R09-01 | CA-01 | 3.1.26 curso 4b / SD-26 | Correcto | Aprobado |
| CP-R09-02 | CA-02 | 3.1.26 curso 5b / SD-26 | Correcto | Aprobado |
| CP-R09-03 | CA-03 | 3.1.26 paso 6 / SD-26 | Correcto | Aprobado |
| CP-R09-04 | CA-04 | 3.1.27 pasos 1–2 / SD-27 | Correcto | Aprobado |
| CP-R09-05 | CA-05 | 3.1.27 curso 4b / SD-27 | Correcto | Aprobado |
| CP-R09-06 | CA-06 | 3.1.27 paso 5 / SD-27 | Correcto | Aprobado |
| CP-R09-07 | CA-07 | 3.1.28 pasos 1–2b / SD-28 | Correcto | Aprobado |
| CP-R09-08 | CA-08 | 3.1.28 curso 2c / SD-28 | Correcto | Aprobado |
| CP-R09-09 | CA-10 | 3.1.29 curso 3b / SD-29 | Correcto | Aprobado |
| CP-R09-10 | CA-09 | 3.1.29 pasos 1–4 / SD-29 | Correcto | Aprobado |
| CP-R09-11 | N-01, N-05 | Listado de promociones | Correcto | Aprobado |
| CP-R09-12 | N-02 | Aplicar a servicio o producto | Correcto | Aprobado |
| CP-R09-13 | N-03, N-06 | 3.1.27 / SD-27 | Correcto | Aprobado |
| CP-R09-14 | N-04 | 3.1.26 / diccionario 4.7.15 | Correcto | Aprobado |

## Casos

### CP-R09-01 · Campos obligatorios
- **Requisito / criterio:** R9.1.2–3 / CA-01
- **Pasos:** 1. *Crear promoción*. 2. Dejar título y descripción vacíos; porcentaje `20`, fechas 30/11/2026 y 31/12/2026. 3. *Guardar promoción*.
- **Resultado esperado:** alerta "Complete los campos obligatorios"; "Campo obligatorio" bajo *Título* y *Descripción*; los demás datos se conservan.
- **Verificación previa:** 2026-10-23 · Correcto · alerta "Complete los campos obligatorios" y "Campo obligatorio" bajo *Título* y *Descripción*.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-02 · Fecha de fin no posterior
- **Requisito / criterio:** R9.1.4–5 / CA-02
- **Pasos:** 1. Título `20 % de descuento en vacunas`, descripción `Descuento para servicios de vacunación`, porcentaje `20`. 2. Fecha de inicio 30/11/2026 y de fin 29/11/2026. 3. *Guardar promoción*. 4. Repetir con la misma fecha en inicio y fin.
- **Resultado esperado:** en ambos intentos, alerta y mensaje bajo *Fecha de fin*: "La fecha de fin debe ser posterior a la de inicio"; no se guarda.
- **Verificación previa:** 2026-10-23 · Correcto · fin 29/11/2026 con inicio 30/11/2026 → alerta y mensaje bajo *Fecha de fin*; la regla también rechaza fechas iguales (prueba de apoyo).
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-03 · Crear promoción
- **Requisito / criterio:** R9.1.6 / CA-03
- **Pasos:** 1. Corregir la fecha de fin a 31/12/2026 y elegir *Consulta general* en *Aplicar a servicio o producto*. 2. *Guardar promoción*.
- **Resultado esperado:** "Promoción creada"; la promoción queda en estado *Borrador*.
- **Verificación previa:** 2026-10-23 · Correcto · "Promoción creada"; `PROM-00001` en estado *Borrador*.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-04 · Buscar promoción
- **Requisito / criterio:** R9.2.1 / CA-04
- **Pasos:** 1. *Editar promoción*. 2. Buscar la promoción de CP-R09-03 (`PROM-…`) y presionar *Buscar promoción*.
- **Resultado esperado:** se muestran título, descripción, porcentaje, servicio asociado y fechas actuales.
- **Verificación previa:** 2026-10-23 · Correcto · se cargaron título, descripción, 20, los tres elementos asociados y las fechas.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-05 · Datos inválidos al editar
- **Requisito / criterio:** R9.2.3–4 / CA-05
- **Pasos:** 1. Cambiar la fecha de fin a 29/11/2026 y *Guardar cambios*. 2. Restaurarla, vaciar el título y guardar. 3. Restaurarlo, poner porcentaje `150` y guardar.
- **Resultado esperado:** en cada intento, "Datos inválidos" con el campo identificado y la regla explicada; no se guarda y, al volver a buscar, la promoción conserva sus datos anteriores.
- **Verificación previa:** 2026-10-23 · Correcto · fin anterior → "Datos inválidos" y "Fin posterior al inicio"; título vacío y descuento 150 verificados en la prueba de apoyo.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-06 · Guardar cambios
- **Requisito / criterio:** R9.2.5 / CA-06
- **Pasos:** 1. Cambiar la descripción a `Descuento para vacunación` y el porcentaje a `25`. 2. *Guardar cambios*.
- **Resultado esperado:** "Promoción actualizada"; al volver a buscarla se ven los datos nuevos.
- **Verificación previa:** 2026-10-23 · Correcto · "Promoción actualizada"; el descuento quedó en 25.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-07 · Publicar promoción
- **Requisito / criterio:** R9.3.1, R9.3.3 / CA-07
- **Pasos:** 1. *Publicar promoción*, seleccionar la promoción. 2. Revisar *Estado actual* y *Vigencia*. 3. *Sí, publicar promoción*.
- **Resultado esperado:** 2 → *Borrador* y `30/11/2026 a 31/12/2026`. 3 → "Promoción visible para clientes"; el estado pasa a *Publicada*.
- **Verificación previa:** 2026-10-23 · Correcto · *Borrador* y `30/11/2026 a 31/12/2026`; "Promoción visible para clientes"; el listado muestra *Publicada*.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-08 · Promoción ya publicada
- **Requisito / criterio:** R9.3.2 / CA-08
- **Pasos:** 1. Seleccionar otra vez la misma promoción en *Publicar promoción* y presionar *Sí, publicar promoción* si está disponible.
- **Resultado esperado:** "La promoción ya está publicada"; estado *Publicada*; solo queda *Volver al listado*; no se modifica nada.
- **Verificación previa:** 2026-10-23 · Correcto · alerta "La promoción ya está publicada", estado *Publicada* y solo *Volver al listado*.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-09 · Cancelar eliminación
- **Requisito / criterio:** R9.4.3 / CA-10
- **Pasos:** 1. *Eliminar promoción*, seleccionar la promoción. 2. *Cancelar*.
- **Resultado esperado:** "Operación cancelada; la promoción se mantiene"; se vuelve al listado y la promoción sigue registrada.
- **Verificación previa:** 2026-10-23 · Correcto · "Operación cancelada; la promoción se mantiene"; vuelve al listado con la promoción.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-10 · Eliminar promoción
- **Requisito / criterio:** R9.4.1–2, R9.4.4 / CA-09
- **Pasos:** 1. *Eliminar promoción*, seleccionar la promoción y revisar su vigencia. 2. *Sí, eliminar promoción*.
- **Resultado esperado:** "Promoción eliminada"; ya no aparece para seleccionar ni en el listado.
- **Verificación previa:** 2026-10-23 · Correcto · "Promoción eliminada"; el listado quedó vacío.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-11 · Listado de promociones
- **Decisiones:** N-01, N-05 (opción A)
- **Pasos:** 1. Abrir *Promociones* en la navegación. 2. Después de publicar una promoción, volver al listado con *Volver al listado*.
- **Resultado esperado:** 1 → listado con código, título, vigencia y estado, y acceso a *Crear*, *Editar*, *Publicar* y *Eliminar*. 2 → el estado muestra *Publicada*; solo existen los estados *Borrador* y *Publicada*.
- **Verificación previa:** 2026-10-23 · Correcto · listado con código, título, vigencia y estado y acciones *Editar*, *Publicar* y *Eliminar*; solo aparecen *Borrador* y *Publicada*.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-12 · Varios servicios y productos
- **Decisión:** N-02 (opción A)
- **Pasos:** 1. *Crear promoción* con *Consulta general* y dos productos en *Aplicar a servicio o producto*. 2. Guardar. 3. Buscarla en *Editar promoción*. 4. Crear otra promoción sin elegir servicio ni producto.
- **Resultado esperado:** 2 → "Promoción creada". 3 → aparecen los tres elementos asociados. 4 → también se guarda (el campo es opcional).
- **Verificación previa:** 2026-10-23 · Correcto · se guardaron *Consulta general*, *Pedigree Adulto Carne 100 g* y *Arena sanitaria Catsan 4 kg*; también se guarda sin elementos.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-13 · Mensajes de Editar y búsqueda por selector
- **Decisiones:** N-03, N-06 (opción A)
- **Pasos:** 1. *Editar promoción*: abrir *Buscar promoción* y escribir parte del título o del código. 2. Elegirla y poner una fecha de fin anterior al inicio. 3. *Guardar cambios*.
- **Resultado esperado:** 1 → el selector filtra las promociones registradas. 3 → alerta "Datos inválidos" y bajo *Fecha de fin* "Fin posterior al inicio".
- **Verificación previa:** 2026-10-23 · Correcto · el selector lista `PROM-00001 — 20 % de descuento en vacunas`; "Datos inválidos" con "Fin posterior al inicio".
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-R09-14 · Descuento entre 0 y 100
- **Decisión:** N-04 (opción A)
- **Pasos:** 1. *Crear promoción*: escribir `150` en *Porcentaje de descuento* y salir del campo. 2. Escribir `-5`.
- **Resultado esperado:** el campo no acepta valores fuera de 0 a 100 (se ajusta al límite).
- **Verificación previa:** 2026-10-23 · Correcto · 150 se ajustó a 100 al salir del campo.
- **Revisión del responsable:** 2026-10-23 · Aprobado
- **Observaciones (funcionamiento y visual):**
