# Pruebas manuales — COR-03 Pantalla de inicio de sesión igual al PDF

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, base PostgreSQL local. Comparar siempre con `Iteracion5_Hospivet.pdf`, págs. 52 y 53.

Resultado de la revisión del responsable: `Aprobado` · `Observado` · `Rechazado`.

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-COR03-01 | CA-01 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-COR03-02 | CA-02 · CA-03 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-COR03-03 | CA-04 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-COR03-04 | CA-05 | Correcto (2026-11-26) | Aprobado (2026-11-26) |

## Casos

### CP-COR03-01 · Posición de la alerta
- **Criterio:** CA-01 (D1)
- **Pasos:** 1. En *Iniciar sesión*, escribir un usuario que no existe y cualquier contraseña. 2. Pulsar *Ingresar*. 3. Repetir con el usuario correcto y una contraseña equivocada.
- **Resultado esperado:** "Usuario no encontrado" y "Contraseña incorrecta" aparecen en la alerta roja debajo de *Contraseña* y encima de *Ingresar*, como en el mockup.
- **Verificación previa:** 2026-11-26 · Correcto · "Usuario no encontrado" aparece debajo de *Contraseña* y encima de *Ingresar*; se conserva lo escrito.
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-COR03-02 · Sin asteriscos ni textos de campo vacío
- **Criterio:** CA-02, CA-03 (D2, D3)
- **Pasos:** 1. Abrir *Iniciar sesión* con los campos vacíos. 2. Pulsar *Ingresar* o Enter.
- **Resultado esperado:** *Usuario* y *Contraseña* sin asterisco; no pasa nada y no aparece ningún texto bajo los campos.
- **Verificación previa:** 2026-11-26 · Correcto · sin asteriscos; con Enter y los campos vacíos no se envía nada ni aparece ningún texto.
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-COR03-03 · Cabecera sin sesión
- **Criterio:** CA-04 (D4)
- **Pasos:** 1. Sin sesión, mirar la cabecera de *Iniciar sesión* y de *Recuperar contraseña*. 2. Iniciar sesión y volver a mirarla.
- **Resultado esperado:** sin sesión no aparece el recuadro del perfil; con sesión aparece "Nombre Apellido · Veterinaria" (o "· Cliente" en el portal).
- **Verificación previa:** 2026-11-26 · Correcto · sin sesión ya no aparece "Acceso veterinario" en ninguna de las dos pantallas; con la cuenta de prueba de la clienta aparece "María Elena Vargas Ríos · Cliente".
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-COR03-04 · El inicio de sesión sigue funcionando
- **Criterio:** CA-05
- **Pasos:** 1. Iniciar sesión con un usuario y contraseña correctos. 2. Cerrar sesión.
- **Resultado esperado:** entra a su pantalla inicial (menú principal o *Reservar cita*) y *Cerrar sesión* vuelve a *Iniciar sesión*.
- **Verificación previa:** 2026-11-26 · Correcto · con la cuenta de prueba de la clienta entra a *Reservar cita*; *Cerrar sesión* vuelve a *Iniciar sesión*. El inicio de sesión del veterinario usa el mismo formulario.
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**
