# Pruebas manuales — R03 Registrar mascota

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, sesión de veterinario iniciada, al menos un cliente registrado, módulo *Mascotas*.

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-R03-01 | CA-01 | Pendiente | Pendiente |
| CP-R03-02 | CA-02 | Pendiente | Pendiente |
| CP-R03-03 | CA-03 | Pendiente | Pendiente |
| CP-R03-04 | CA-03 | Pendiente | Pendiente |
| CP-R03-05 | CA-04 | Pendiente | Pendiente |

## Casos

### CP-R03-01 · Formulario y edad calculada
- **Requisito / criterio:** R3.1.1 / CA-01
- **Pasos:** 1. Abrir *Mascotas*. 2. Elegir una fecha de nacimiento de hace 3 años.
- **Resultado esperado:** *Edad calculada* muestra "3 años"; el calendario no permite elegir fechas futuras.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R03-02 · Campos obligatorios
- **Requisito / criterio:** R3.1.2–3 / CA-02
- **Pasos:** 1. Escribir solo el Nombre. 2. Presionar *Guardar mascota*.
- **Resultado esperado:** alerta "Complete los campos obligatorios"; el nombre se conserva.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R03-03 · Peso inválido
- **Requisito / criterio:** R3.1.4–5 / CA-03
- **Pasos:** 1. Llenar propietario, nombre y especie; peso `0`. 2. Guardar. 3. Repetir con peso `-2`.
- **Resultado esperado:** en ambos casos alerta "Edad o peso inválido"; no se guarda.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R03-04 · Fecha de nacimiento futura (después de las 20:00)
- **Requisito / criterio:** R3.1.4–5 / CA-03
- **Precondición:** realizar entre las 20:00 y las 23:59 (caso corregido AUD-02).
- **Pasos:** 1. Escribir manualmente en la fecha el día de mañana. 2. Guardar.
- **Resultado esperado:** "Edad o peso inválido"; no se guarda.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R03-05 · Registro correcto
- **Requisito / criterio:** R3.1.6–7 / CA-04
- **Pasos:** 1. Propietario `Ana Rojas`, Nombre `Firulais`, Especie `Perro`, Peso `12.5`, Sexo `Macho`. 2. Guardar. 3. Ir a *Citas* → *Registrar cita* y elegir la propietaria.
- **Resultado esperado:** mensaje "Mascota registrada y vinculada al propietario"; `Firulais` aparece como mascota de Ana Rojas.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**
