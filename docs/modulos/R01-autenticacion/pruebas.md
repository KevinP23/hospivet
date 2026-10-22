# Pruebas manuales — R01 Autenticación y acceso

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, base PostgreSQL local con la cuenta veterinaria inicial creada.

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-R01-01 | CA-01 | Pendiente | Pendiente |
| CP-R01-02 | CA-02 | Pendiente | Pendiente |
| CP-R01-03 | CA-03 | Pendiente | Pendiente |
| CP-R01-04 | CA-04 | Pendiente | Pendiente |
| CP-R01-05 | CA-05 | Pendiente | Pendiente |
| CP-R01-06 | CA-06 | Pendiente | Pendiente |

## Casos

### CP-R01-01 · Campos obligatorios
- **Requisito / criterio:** R1.1.1 / CA-01
- **Precondición:** sin sesión iniciada.
- **Pasos:** 1. Abrir `http://127.0.0.1:5173/`. 2. Presionar *Ingresar* con ambos campos vacíos.
- **Resultado esperado:** debajo de cada campo aparece "Ingrese el usuario" e "Ingrese la contraseña"; no se envía nada.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R01-02 · Usuario inexistente
- **Requisito / criterio:** R1.1.2–3 / CA-02
- **Pasos:** 1. Escribir un usuario que no existe y cualquier contraseña. 2. Presionar *Ingresar*.
- **Resultado esperado:** alerta roja "Usuario no encontrado"; los datos escritos se conservan.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R01-03 · Contraseña incorrecta
- **Requisito / criterio:** R1.1.4–5 / CA-03
- **Pasos:** 1. Escribir el usuario veterinario correcto y una contraseña equivocada. 2. Presionar *Ingresar*.
- **Resultado esperado:** alerta roja "Contraseña incorrecta"; no se abre el sistema.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R01-04 · Acceso correcto
- **Requisito / criterio:** R1.1.6 / CA-04
- **Pasos:** 1. Escribir usuario y contraseña correctos. 2. Presionar *Ingresar* (o Enter).
- **Resultado esperado:** se abre *Registrar cliente*; la cabecera muestra el usuario y la navegación con los módulos habilitados.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R01-05 · Rutas protegidas
- **Requisito / criterio:** CA-05
- **Pasos:** 1. Sin sesión, escribir en el navegador `http://127.0.0.1:5173/citas`.
- **Resultado esperado:** el sistema redirige al inicio de sesión.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R01-06 · Cerrar sesión
- **Requisito / criterio:** CA-06
- **Pasos:** 1. Con sesión iniciada, abrir el menú del perfil y elegir *Cerrar sesión*. 2. Intentar volver con el botón *Atrás* del navegador.
- **Resultado esperado:** vuelve al inicio de sesión y no permite entrar a los módulos sin ingresar de nuevo.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**
