# Pruebas manuales — R02 Gestión de registro de cliente

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, sesión de veterinario iniciada, módulo *Clientes*.

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-R02-01 | CA-01 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-R02-02 | CA-02 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-R02-03 | CA-03 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-R02-04 | CA-04 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-R02-05 | CA-05 | Correcto (2026-11-26) | Aprobado (2026-11-26) |

## Casos

### CP-R02-01 · Formulario completo
- **Requisito / criterio:** R2.1.1 / CA-01
- **Pasos:** 1. Abrir *Clientes*.
- **Resultado esperado:** campos Nombre, Apellido, Carnet de identidad, Teléfono, Correo y Dirección en dos columnas; botones *Guardar cliente* y *Cancelar*; referencia R2.1 visible.
- **Verificación previa:** 2026-11-26 · Correcto · *Registrar cliente* con Nombre, Apellido, Carnet de identidad, Teléfono, Correo y Dirección en dos columnas, *Guardar cliente* y *Cancelar* y "Referencias: R2.1, R2.1.1–6". Diferencia con el mockup (pág. 64): asteriscos rojos en los obligatorios (D1).
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-R02-02 · Campos obligatorios
- **Requisito / criterio:** R2.1.2–3 / CA-02
- **Pasos:** 1. Llenar solo Nombre y Correo. 2. Presionar *Guardar cliente*.
- **Resultado esperado:** alerta "Complete los campos obligatorios" y "Campo obligatorio" bajo cada campo faltante, sin asteriscos (COR-04); Nombre y Correo siguen escritos.
- **Verificación previa:** 2026-11-26 · Correcto · solo Nombre y Correo: alerta "Complete los campos obligatorios", Apellido, CI y Teléfono marcados; Nombre y Correo se conservan. Diferencia con el mockup (pág. 65): bajo cada campo dice "Complete los campos obligatorios" y en el PDF "Campo obligatorio" (D2).
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-R02-03 · CI inválido
- **Requisito / criterio:** R2.1.4–5 / CA-03
- **Pasos:** 1. Llenar todos los obligatorios con CI `12AB34`. 2. Presionar *Guardar cliente*.
- **Resultado esperado:** alerta "CI inválido"; no se guarda.
- **Verificación previa:** 2026-11-26 · Correcto · CI `12AB34`: alerta "CI inválido" y el mismo texto bajo el campo, igual que el mockup de la pág. 64; no se guarda.
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-R02-04 · CI duplicado
- **Requisito / criterio:** CA-04
- **Precondición:** existe un cliente con CI `7654321` (registrarlo en CP-R02-05 primero).
- **Pasos:** 1. Registrar otro cliente con CI `7654321`.
- **Resultado esperado:** alerta "El CI ya está registrado"; el formulario conserva los datos.
- **Verificación previa:** 2026-11-26 · Correcto · segundo registro con CI `7654321`: alerta "El CI ya está registrado"; se conservan los datos. Ese mensaje no está en el PDF (D5).
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-R02-05 · Registro correcto
- **Requisito / criterio:** R2.1.6 / CA-05
- **Pasos:** 1. Llenar Nombre `Ana`, Apellido `Rojas`, CI `7654321`, Teléfono `70000000`. 2. Presionar *Guardar cliente*. 3. Ir a *Mascotas* y abrir el selector de propietario.
- **Resultado esperado:** mensaje "Cliente registrado" (SD-03, COR-04), formulario vacío; `Ana Rojas — CI 7654321` aparece como propietario disponible.
- **Verificación previa:** 2026-11-26 · Correcto · Ana Rojas, CI 7654321, teléfono 70000000: aviso "Cliente registrado correctamente" y formulario vacío; en *Mascotas* aparece "Ana Rojas — CI 7654321" como propietaria. Diferencia: SD-03 dice "Cliente registrado" (D3).
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**
