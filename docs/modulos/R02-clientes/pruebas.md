# Pruebas manuales — R02 Gestión de registro de cliente

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, sesión de veterinario iniciada, módulo *Clientes*.

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-R02-01 | CA-01 | Pendiente | Pendiente |
| CP-R02-02 | CA-02 | Pendiente | Pendiente |
| CP-R02-03 | CA-03 | Pendiente | Pendiente |
| CP-R02-04 | CA-04 | Pendiente | Pendiente |
| CP-R02-05 | CA-05 | Pendiente | Pendiente |

## Casos

### CP-R02-01 · Formulario completo
- **Requisito / criterio:** R2.1.1 / CA-01
- **Pasos:** 1. Abrir *Clientes*.
- **Resultado esperado:** campos Nombre, Apellido, Carnet de identidad, Teléfono, Correo y Dirección en dos columnas; botones *Guardar cliente* y *Cancelar*; referencia R2.1 visible.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R02-02 · Campos obligatorios
- **Requisito / criterio:** R2.1.2–3 / CA-02
- **Pasos:** 1. Llenar solo Nombre y Correo. 2. Presionar *Guardar cliente*.
- **Resultado esperado:** alerta "Complete los campos obligatorios", campos faltantes marcados; Nombre y Correo siguen escritos.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R02-03 · CI inválido
- **Requisito / criterio:** R2.1.4–5 / CA-03
- **Pasos:** 1. Llenar todos los obligatorios con CI `12AB34`. 2. Presionar *Guardar cliente*.
- **Resultado esperado:** alerta "CI inválido"; no se guarda.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R02-04 · CI duplicado
- **Requisito / criterio:** CA-04
- **Precondición:** existe un cliente con CI `7654321` (registrarlo en CP-R02-05 primero).
- **Pasos:** 1. Registrar otro cliente con CI `7654321`.
- **Resultado esperado:** alerta "El CI ya está registrado"; el formulario conserva los datos.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R02-05 · Registro correcto
- **Requisito / criterio:** R2.1.6 / CA-05
- **Pasos:** 1. Llenar Nombre `Ana`, Apellido `Rojas`, CI `7654321`, Teléfono `70000000`. 2. Presionar *Guardar cliente*. 3. Ir a *Mascotas* y abrir el selector de propietario.
- **Resultado esperado:** mensaje "Cliente registrado correctamente", formulario vacío; `Ana Rojas — CI 7654321` aparece como propietario disponible.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**
