# Pruebas manuales — COR-04 Pantalla Registrar cliente igual al PDF

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, base PostgreSQL local, sesión de veterinario. Comparar con `Iteracion5_Hospivet.pdf`, págs. 64 y 65, y SD-03 (pág. 66).

Resultado de la revisión del responsable: `Aprobado` · `Observado` · `Rechazado`.

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-COR04-01 | CA-01 · CA-02 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-COR04-02 | CA-03 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-COR04-03 | CA-04 | Correcto (2026-11-26) | Aprobado (2026-11-26) |

## Casos

### CP-COR04-01 · Campos obligatorios como en el mockup
- **Criterio:** CA-01, CA-02 (D1, D2)
- **Pasos:** 1. Abrir *Clientes*. 2. Llenar Apellido, Teléfono, Correo y Dirección, y dejar vacíos Nombre y Carnet de identidad. 3. Pulsar *Guardar cliente*.
- **Resultado esperado:** sin asteriscos; alerta "Complete los campos obligatorios" y "Campo obligatorio" bajo Nombre y Carnet de identidad, como en la pág. 65; se conserva lo escrito.
- **Verificación previa:** 2026-11-26 · Correcto · igual que el mockup de la pág. 65.
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-COR04-02 · Aviso de registro
- **Criterio:** CA-03 (D3)
- **Pasos:** 1. Completar Nombre y un CI nuevo. 2. Pulsar *Guardar cliente*.
- **Resultado esperado:** aparece "Cliente registrado" (SD-03) y el formulario se limpia.
- **Verificación previa:** 2026-11-26 · Correcto · se registró el cliente de prueba Luis Mamani (CI 8123456); aparece "Cliente registrado" y el formulario queda vacío.
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-COR04-03 · Mensajes que se mantienen
- **Criterio:** CA-04 (D4, D5)
- **Pasos:** 1. Intentar guardar con CI `12AB34`. 2. Con un correo mal escrito. 3. Con un CI ya registrado.
- **Resultado esperado:** "CI inválido", "Correo inválido" y "El CI ya está registrado", como antes de COR-04.
- **Verificación previa:** 2026-11-26 · Correcto · COR-04 no cambió esas reglas; "CI inválido" y "El CI ya está registrado" se comprobaron en la revisión de R02 del mismo día (CP-R02-03 y CP-R02-04).
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**
