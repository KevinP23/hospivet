# Pruebas manuales — R05 Gestión de citas

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, sesión de veterinario iniciada, cliente `Ana Rojas` con la mascota `Firulais` y otro cliente con su propia mascota, módulo *Citas*.

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-R05-01 | CA-08, CA-09 | Pendiente | Pendiente |
| CP-R05-02 | CA-01 | Pendiente | Pendiente |
| CP-R05-03 | CA-02 | Pendiente | Pendiente |
| CP-R05-04 | CA-04 | Pendiente | Pendiente |
| CP-R05-05 | CA-03 | Pendiente | Pendiente |
| CP-R05-06 | CA-05 | Pendiente | Pendiente |
| CP-R05-07 | CA-07 | Pendiente | Pendiente |
| CP-R05-08 | CA-06, CA-10 | Pendiente | Pendiente |
| CP-R05-09 | CA-11 | Pendiente | Pendiente |

## Casos

### CP-R05-01 · Listado y día sin citas
- **Requisito / criterio:** R5.4 / CA-08, CA-09
- **Pasos:** 1. Abrir *Citas*. 2. Elegir un día sin citas y presionar *Filtrar citas*.
- **Resultado esperado:** el filtro inicia en la fecha de hoy; para el día sin citas aparece "No hay citas registradas para la fecha seleccionada" y la fecha elegida se conserva.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R05-02 · Mascotas del cliente
- **Requisito / criterio:** R5.1.1 / CA-01
- **Pasos:** 1. *Registrar cita*. 2. Abrir *Mascota* sin cliente. 3. Elegir `Ana Rojas` y abrir *Mascota*. 4. Cambiar a otro cliente.
- **Resultado esperado:** 2 → selector deshabilitado. 3 → solo `Firulais`. 4 → la mascota elegida se limpia y aparecen las del otro cliente.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R05-03 · Campos obligatorios
- **Requisito / criterio:** R5.1.2 / CA-02
- **Pasos:** 1. Elegir solo cliente y mascota. 2. Presionar *Registrar cita*.
- **Resultado esperado:** "Complete los campos obligatorios"; cliente y mascota se conservan.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R05-04 · Registrar cita
- **Requisito / criterio:** R5.1.5 / CA-04
- **Pasos:** 1. Cliente `Ana Rojas`, mascota `Firulais`, servicio *Consulta general*, fecha de mañana, hora `10:00`, motivo `Control anual`. 2. Registrar. 3. Filtrar el día de mañana.
- **Resultado esperado:** "Cita registrada correctamente"; la cita aparece a las 10:00 en estado *Programada*.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R05-05 · Horario ocupado
- **Requisito / criterio:** R5.1.3–4 / CA-03
- **Pasos:** 1. Registrar otra cita para mañana a las `10:00` con otro cliente. 2. Repetir con la hora `10:15`.
- **Resultado esperado:** 1 → "Horario no disponible, seleccione otro" y los datos se conservan. 2 → también "Horario no disponible, seleccione otro", porque se solapa con la cita de 30 min (D-11).
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R05-06 · Modificar cita
- **Requisito / criterio:** R5.2 / CA-05
- **Pasos:** 1. *Modificar* en la cita de las 10:00. 2. Cambiar la hora a `11:00` y guardar. 3. Modificarla otra vez a una hora ya ocupada.
- **Resultado esperado:** 2 → "Cambios de la cita guardados" y aparece a las 11:00. 3 → "Horario no disponible, seleccione otro".
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R05-07 · No confirmar cancelación
- **Requisito / criterio:** R5.3.3 / CA-07
- **Pasos:** 1. *Cancelar* en una cita programada. 2. *Volver al listado*.
- **Resultado esperado:** la cita sigue *Programada*.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R05-08 · Cancelar cita y liberar horario
- **Requisito / criterio:** R5.3.1–2, R5.3.4 / CA-06, CA-10
- **Pasos:** 1. *Cancelar* en la cita de las 11:00 y confirmar con *Sí, cancelar cita*. 2. Registrar una nueva cita mañana a las `11:00`.
- **Resultado esperado:** 1 → "Cita cancelada correctamente"; queda *Cancelada*, sin botones de acción. 2 → se registra sin conflicto.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R05-09 · Cita en fecha pasada
- **Requisito / criterio:** CA-11 (D-10)
- **Pasos:** 1. Abrir el calendario de *Registrar cita*. 2. Elegir hoy con una hora que ya pasó y registrar.
- **Resultado esperado:** 1 → los días anteriores están deshabilitados. 2 → "Seleccione una fecha y hora futuras"; no se guarda.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**
