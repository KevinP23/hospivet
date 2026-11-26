# Pruebas manuales — R01 Autenticación y acceso

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, base PostgreSQL local con la cuenta veterinaria inicial creada.

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-R01-01 | CA-01 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-R01-02 | CA-02 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-R01-03 | CA-03 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-R01-04 | CA-04 | Correcto (2026-11-24) | Aprobado (2026-11-26) |
| CP-R01-05 | CA-05 | Correcto (2026-11-26) | Aprobado (2026-11-26) |
| CP-R01-06 | CA-06 | Correcto (2026-11-26) | Aprobado (2026-11-26) |

## Casos

### CP-R01-01 · Campos obligatorios
- **Requisito / criterio:** R1.1.1 / CA-01
- **Precondición:** sin sesión iniciada.
- **Pasos:** 1. Abrir `http://127.0.0.1:5173/`. 2. Presionar *Ingresar* con ambos campos vacíos.
- **Resultado esperado:** no se envía nada y no aparece ningún texto bajo los campos, que no tienen asterisco (COR-03, D-22; antes mostraba "Ingrese el usuario" e "Ingrese la contraseña").
- **Verificación previa:** 2026-11-26 · Correcto · con ambos campos vacíos aparecen "Ingrese el usuario" e "Ingrese la contraseña" y no se envía nada. Diferencia con el mockup (pág. 52): esos textos y los asteriscos rojos de *Usuario* y *Contraseña* no están en el PDF (ver revisiones).
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-R01-02 · Usuario inexistente
- **Requisito / criterio:** R1.1.2–3 / CA-02
- **Pasos:** 1. Escribir un usuario que no existe y cualquier contraseña. 2. Presionar *Ingresar*.
- **Resultado esperado:** alerta roja "Usuario no encontrado" debajo de *Contraseña* y encima de *Ingresar* (COR-03); los datos escritos se conservan.
- **Verificación previa:** 2026-11-26 · Correcto · `vet.desconocido` muestra la alerta roja "Usuario no encontrado" y conserva lo escrito. Diferencia con el mockup: la alerta aparece arriba de los campos; en el PDF va debajo de *Contraseña*, encima de *Ingresar*.
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-R01-03 · Contraseña incorrecta
- **Requisito / criterio:** R1.1.4–5 / CA-03
- **Pasos:** 1. Escribir el usuario veterinario correcto y una contraseña equivocada. 2. Presionar *Ingresar*.
- **Resultado esperado:** alerta roja "Contraseña incorrecta"; no se abre el sistema.
- **Verificación previa:** 2026-11-26 · Correcto · `kpinto` con una contraseña equivocada muestra "Contraseña incorrecta" y no entra (misma diferencia de posición de la alerta).
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-R01-04 · Acceso correcto
- **Requisito / criterio:** R1.1.6 / CA-04
- **Pasos:** 1. Escribir usuario y contraseña correctos. 2. Presionar *Ingresar* (o Enter).
- **Resultado esperado:** se abre el *Menú principal* (COR-02); la cabecera muestra nombre y apellido del veterinario y la navegación con los módulos habilitados.
- **Verificación previa:** 2026-11-24 · Correcto · con `kpinto` y su contraseña se abrió el *Menú principal* con "Kevin Fernando Pinto Carrasco · Veterinaria" y la navegación completa. No se repitió el 2026-11-26 porque la contraseña actual (temporal de R1.2) solo la tiene el responsable; la prueba automática `completaFlujoLoginClienteYMascota` lo confirma.
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-R01-05 · Rutas protegidas
- **Requisito / criterio:** CA-05
- **Pasos:** 1. Sin sesión, escribir en el navegador `http://127.0.0.1:5173/citas`.
- **Resultado esperado:** el sistema redirige al inicio de sesión.
- **Verificación previa:** 2026-11-26 · Correcto · sin sesión, `http://127.0.0.1:5173/citas` redirige a *Iniciar sesión*.
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**

### CP-R01-06 · Cerrar sesión
- **Requisito / criterio:** CA-06
- **Pasos:** 1. Con sesión iniciada, abrir el menú del perfil y elegir *Cerrar sesión*. 2. Intentar volver con el botón *Atrás* del navegador.
- **Resultado esperado:** vuelve al inicio de sesión y no permite entrar a los módulos sin ingresar de nuevo.
- **Verificación previa:** 2026-11-26 · Correcto · con la cuenta de prueba de la clienta (el cierre de sesión es el mismo para ambos roles): *Cerrar sesión* vuelve a *Iniciar sesión*; con *Atrás* el navegador vuelve a *Iniciar sesión* y el servidor responde 401 a la sesión.
- **Revisión del responsable:** Aprobado (2026-11-26)
- **Observaciones (funcionamiento y visual):**
