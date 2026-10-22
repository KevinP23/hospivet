# Pruebas manuales — COR-01 Correcciones de la auditoría

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, base PostgreSQL local con la migración V5 aplicada, sesión de veterinario, cliente `Ana Rojas` con la mascota `Firulais`, otro cliente con su mascota y productos de demostración.

Los casos de R01 a R05 se vuelven a ejecutar completos; aquí solo están los que verifican cada corrección.

La verificación previa se hizo con una base H2 temporal en memoria y una cuenta veterinaria de prueba creada solo para esa sesión; no se usó la base PostgreSQL del responsable.

## Observaciones de la verificación previa

- **O-1:** si se escribe a mano una fecha no permitida (por ejemplo, mañana en *Fecha de nacimiento*), el calendario la descarta en silencio y conserva el valor anterior. No se guardan datos inválidos, pero el usuario podría no notarlo. Se deja a criterio del responsable.
- **O-2:** al registrar una cita para otro día, el listado vuelve filtrado por la fecha anterior y no la muestra (mejora opcional OP-03, no incluida).

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-COR-01 | CA-01 | Correcto | Aprobado |
| CP-COR-02 | CA-02 | Correcto | Aprobado |
| CP-COR-03 | CA-03 | Correcto | Aprobado |
| CP-COR-04 | CA-04 | Correcto | Aprobado |
| CP-COR-05 | CA-05 | Parcial | Aprobado |
| CP-COR-06 | CA-06 | Correcto | Aprobado |
| CP-COR-07 | CA-07 | Correcto | Aprobado |
| CP-COR-08 | CA-08 | No ejecutada | Aprobado |
| CP-COR-09 | CA-10 | Correcto | Aprobado |
| CP-COR-10 | CA-12 | Correcto | Aprobado |
| CP-COR-11 | CA-13 | Correcto | Aprobado |
| CP-COR-12 | CA-16 | No ejecutada | Aprobado |

## Casos

### CP-COR-01 · Nueva cantidad vacía
- **Criterio:** CA-01 (AUD-01)
- **Pasos:** 1. *Inventario* → *Stock* en un producto con existencia 8. 2. Borrar *Nueva cantidad*. 3. *Guardar cambios*.
- **Resultado esperado:** "Cantidad inválida"; al volver al listado la existencia sigue en 8.
- **Verificación previa:** 2026-10-21 · Correcto · vacío → "Cantidad inválida"; la existencia no cambió.
- **Revisión del responsable:** 2026-10-22 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-COR-02 · Fecha local después de las 20:00
- **Criterio:** CA-02 (AUD-02)
- **Precondición:** realizar entre las 20:00 y las 23:59.
- **Pasos:** 1. Abrir *Citas*. 2. Abrir *Mascotas* e intentar registrar con fecha de nacimiento de mañana.
- **Resultado esperado:** 1 → el filtro muestra la fecha de hoy. 2 → "Edad o peso inválido" o el calendario no permite la fecha.
- **Verificación previa:** 2026-10-21 · Correcto · a las 20:42 (UTC ya era día 22) el filtro mostró 21/10/2026; el calendario no acepta la fecha de mañana escrita a mano (ver observación O-1).
- **Revisión del responsable:** 2026-10-22 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-COR-03 · Citas solo a futuro
- **Criterio:** CA-03 (AUD-03, D-10)
- **Pasos:** 1. *Registrar cita*: abrir el calendario. 2. Elegir hoy con una hora que ya pasó y registrar. 3. *Modificar* una cita programada y poner una hora pasada de hoy.
- **Resultado esperado:** 1 → los días anteriores aparecen deshabilitados. 2 y 3 → "Seleccione una fecha y hora futuras"; no se guarda y los datos se conservan.
- **Verificación previa:** 2026-10-21 · Correcto · hoy 08:00 → "Seleccione una fecha y hora futuras" en alerta y en el campo Hora; los datos se conservaron. El servidor también la rechaza.
- **Revisión del responsable:** 2026-10-22 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-COR-04 · Solapamiento por duración
- **Criterio:** CA-04 (AUD-04, D-11)
- **Precondición:** cita programada mañana a las 10:00 con *Consulta general* (30 min).
- **Pasos:** 1. Registrar otra cita mañana a las 10:15. 2. Registrar a las 09:45. 3. Registrar a las 10:30.
- **Resultado esperado:** 1 y 2 → "Horario no disponible, seleccione otro". 3 → se registra.
- **Verificación previa:** 2026-10-21 · Correcto · con cita a las 10:00: 10:15 y 09:45 → "Horario no disponible, seleccione otro"; 10:30 → registrada.
- **Revisión del responsable:** 2026-10-22 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-COR-05 · Existencia anterior y resultante
- **Criterio:** CA-05 (AUD-05)
- **Pasos:** 1. Actualizar el stock de un producto de 8 a 5. 2. Consultar la tabla `movimiento_inventario` (pgAdmin o `psql`).
- **Resultado esperado:** el último movimiento tiene `stock_anterior = 8`, `stock_resultante = 5`, `cantidad = 3`, tipo `AJUSTE`; los movimientos antiguos también tienen ambos valores.
- **Verificación previa:** 2026-10-21 · Parcial · el movimiento guarda 42 → 5 con cantidad 37 (prueba de apoyo) y V5 se aplicó sin errores; falta revisar en PostgreSQL el completado de movimientos antiguos.
- **Revisión del responsable:** 2026-10-22 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-COR-06 · Sesión vencida
- **Criterio:** CA-06 (AUD-06)
- **Pasos:** 1. Iniciar sesión y abrir *Inventario*. 2. Reiniciar el backend (la sesión se pierde). 3. Presionar *Buscar*.
- **Resultado esperado:** el sistema vuelve al inicio de sesión con el aviso "Su sesión expiró, inicie sesión nuevamente".
- **Verificación previa:** 2026-10-21 · Correcto · tras reiniciar el backend, *Buscar* llevó a inicio de sesión con "Su sesión expiró, inicie sesión nuevamente".
- **Revisión del responsable:** 2026-10-22 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-COR-07 · Categorías y búsqueda de stock
- **Criterio:** CA-07 (AUD-07)
- **Pasos:** 1. Filtrar por *Higiene para gatos* y volver a abrir el selector de categorías. 2. Con ese filtro activo, *Stock* → buscar `pedigree`. 3. Buscar `carne`.
- **Resultado esperado:** 1 → siguen todas las categorías. 2 → encuentra *Pedigree Adulto Carne 100 g* aunque no esté en el listado filtrado. 3 → muestra un selector con los productos que coinciden para elegir uno.
- **Verificación previa:** 2026-10-21 · Correcto · con el filtro *Higiene para gatos* siguen las 7 categorías; `carne` mostró un selector con 2 coincidencias y se actualizó Pedigree a 5 (estado Stock bajo).
- **Revisión del responsable:** 2026-10-22 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-COR-08 · Listado con datos inactivos
- **Criterio:** CA-08 (AUD-08)
- **Pasos:** 1. En la base, marcar como inactivo (`activo = false`) un cliente que tenga citas. 2. Abrir *Citas* → *Ver todas*. 3. Volver a activarlo.
- **Resultado esperado:** el listado se muestra completo, incluida la cita de ese cliente.
- **Verificación previa:** 2026-10-21 · No ejecutada · requiere desactivar un cliente directamente en la base.
- **Revisión del responsable:** 2026-10-22 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-COR-09 · Validaciones de formato
- **Criterio:** CA-10 (AUD-10)
- **Pasos:** 1. Registrar cliente con correo `ana@` . 2. Registrar mascota con peso `12345`. 3. Agregar producto con precio `10.555`.
- **Resultado esperado:** 1 → "Correo inválido". 2 → "Edad o peso inválido". 3 → "Precio o cantidad inválidos". Ninguno se guarda y los datos se conservan.
- **Verificación previa:** 2026-10-21 · Correcto · correo `ana@` → "Correo inválido"; peso `12345` → "Edad o peso inválido". Precio `10.555` cubierto por la validación del servidor (no ejecutado en pantalla).
- **Revisión del responsable:** 2026-10-22 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-COR-10 · Orden de verificación del inicio de sesión
- **Criterio:** CA-12 (AUD-12)
- **Precondición:** existe una cuenta con rol `CLIENTE` (se crea en la base solo para esta prueba y se elimina después).
- **Pasos:** 1. Ingresar con esa cuenta y una contraseña incorrecta. 2. Ingresar con su contraseña correcta.
- **Resultado esperado:** 1 → "Contraseña incorrecta". 2 → mensaje de acceso habilitado solo para veterinarios.
- **Verificación previa:** 2026-10-21 · Correcto · prueba de apoyo: cuenta CLIENTE con contraseña incorrecta → "Contraseña incorrecta"; con la correcta → acceso no habilitado.
- **Revisión del responsable:** 2026-10-22 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-COR-11 · Arranque limpio del backend
- **Criterio:** CA-13 (AUD-13)
- **Pasos:** 1. Iniciar el backend y revisar la consola.
- **Resultado esperado:** no aparece `Using generated security password`; el inicio de sesión sigue funcionando.
- **Verificación previa:** 2026-10-21 · Correcto · el arranque ya no muestra `Using generated security password`.
- **Revisión del responsable:** 2026-10-22 · Aprobado
- **Observaciones (funcionamiento y visual):**

### CP-COR-12 · Motivo obligatorio
- **Criterio:** CA-16 (D-12)
- **Pasos:** 1. Registrar una cita completa dejando vacío el motivo.
- **Resultado esperado:** "Complete los campos obligatorios"; no se guarda.
- **Verificación previa:** 2026-10-21 · No ejecutada · regla sin cambios en esta corrección.
- **Revisión del responsable:** 2026-10-22 · Aprobado
- **Observaciones (funcionamiento y visual):**
