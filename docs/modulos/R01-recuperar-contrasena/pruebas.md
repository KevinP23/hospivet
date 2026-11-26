# Pruebas manuales — R1.2 Recuperar contraseña

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, base PostgreSQL local, servidor de correo configurado (`HOSPIVET_MAIL_*`) y la cuenta veterinaria con un correo real registrado.

Resultado de la revisión del responsable: `Aprobado` · `Observado` · `Rechazado`.

Los casos usan las opciones aprobadas en el plan (D-20), sin agregados en pantalla. En todos los casos se revisa además que la pantalla tenga exactamente los elementos del mockup del PDF.

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-R12-01 | CA-01 | Correcto (2026-11-25) | Aprobado (2026-11-25) |
| CP-R12-02 | CA-02 | Correcto (2026-11-25) | Aprobado (2026-11-25) |
| CP-R12-03 | CA-03 | Realizada por el responsable (2026-11-25) | Aprobado (2026-11-25) |
| CP-R12-04 | CA-03 | Realizada por el responsable (2026-11-25) | Aprobado (2026-11-25) |
| CP-R12-05 | CA-04 | Realizada por el responsable (2026-11-25) | Aprobado (2026-11-25) |
| CP-R12-06 | CA-05 | Correcto (2026-11-25) | Aprobado (2026-11-25) |
| CP-R12-07 | N-05 | Correcto (2026-11-25) | Aprobado (2026-11-25) |
| CP-R12-08 | CA-06 | Correcto (2026-11-25) | Aprobado (2026-11-25) |

## Casos

### CP-R12-01 · Abrir Recuperar contraseña

- **Requisito / criterio:** R1.2.1 / CA-01
- **Precondición:** sesión cerrada, pantalla *Iniciar sesión*.
- **Pasos:**
  1. Pulsar "Recuperar contraseña".
- **Resultado esperado:** se abre *Recuperar contraseña* con el logo HV, el título, el texto "Ingresa tu usuario o correo registrado", el campo *Usuario o correo registrado*, el botón *Enviar contraseña temporal* y el enlace *Volver al inicio de sesión*. No hay ningún otro elemento.
- **Verificación previa:** 2026-11-25 · Correcto · desde *Iniciar sesión*, "Recuperar contraseña" abre la pantalla con exactamente los elementos del mockup; *Menú principal* aparece marcado como en el PDF.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R12-02 · Usuario no registrado

- **Requisito / criterio:** R1.2.2, R1.2.3 / CA-02
- **Precondición:** pantalla *Recuperar contraseña*.
- **Pasos:**
  1. Escribir `vet.desconocido` y pulsar *Enviar contraseña temporal*.
  2. Volver a *Iniciar sesión* y entrar con la contraseña de siempre.
- **Resultado esperado:** aparece "Usuario no registrado" en rojo bajo el campo, se conserva lo escrito y no llega ningún correo. La contraseña de siempre sigue funcionando.
- **Verificación previa:** 2026-11-25 · Correcto · `vet.desconocido` muestra "Usuario no registrado" en rojo bajo el campo, igual que el mockup, y conserva lo escrito. La prueba automática confirma que no cambia ninguna contraseña.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R12-03 · Recuperar con el nombre de usuario

- **Requisito / criterio:** R1.2.2, R1.2.4 / CA-03
- **Precondición:** la cuenta veterinaria tiene correo registrado.
- **Pasos:**
  1. Escribir el nombre de usuario (por ejemplo, `kpinto`) y pulsar *Enviar contraseña temporal*.
  2. Revisar la bandeja del correo registrado.
- **Resultado esperado:** aparece "Recuperación solicitada" y llega el correo "HOSPIVET · Contraseña temporal" con el usuario y una contraseña temporal de 8 caracteres (10 hasta el ajuste del 2026-11-25).
- **Verificación previa:** 2026-11-25 · Realizada por el responsable con su correo real; el registro del backend no muestra errores de envío. La prueba automática con un envío simulado confirma la contraseña temporal de 10 caracteres, el destinatario y el asunto.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R12-04 · Recuperar con el correo registrado

- **Requisito / criterio:** R1.2.2, R1.2.4 / CA-03 (N-01)
- **Precondición:** la cuenta veterinaria tiene correo registrado.
- **Pasos:**
  1. Escribir el correo registrado, con alguna letra en mayúscula, y pulsar *Enviar contraseña temporal*.
- **Resultado esperado:** igual que CP-R12-03.
- **Verificación previa:** 2026-11-25 · Realizada por el responsable junto con CP-R12-03. La prueba automática confirma que el correo se reconoce sin distinguir mayúsculas.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R12-05 · Entrar con la contraseña temporal

- **Requisito / criterio:** postcondición 3.1.2 / CA-04
- **Precondición:** se recibió una contraseña temporal (CP-R12-03 o CP-R12-04).
- **Pasos:**
  1. En *Iniciar sesión*, entrar con la contraseña anterior.
  2. Entrar con la contraseña temporal del correo.
- **Resultado esperado:** con la anterior aparece "Contraseña incorrecta"; con la temporal se abre el menú principal.
- **Verificación previa:** 2026-11-25 · Realizada por el responsable junto con CP-R12-03. La prueba automática confirma que la contraseña anterior deja de funcionar ("Contraseña incorrecta") y la temporal permite entrar.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R12-06 · El correo no se puede enviar

- **Requisito / criterio:** postcondición 3.1.2 / CA-05 (N-03)
- **Precondición:** backend iniciado sin las variables `HOSPIVET_MAIL_*` (o sin internet).
- **Pasos:**
  1. Pedir la recuperación con un usuario registrado.
  2. Entrar con la contraseña de siempre.
- **Resultado esperado:** aparece la alerta general "No fue posible completar la operación" y la contraseña de siempre sigue funcionando.
- **Verificación previa:** 2026-11-25 · Correcto en la prueba automática: con el envío fallando dentro de una transacción real, la operación se deshace, responde "No fue posible completar la operación" y la contraseña anterior sigue funcionando. No se repitió en la aplicación porque habría que reiniciar el backend sin el correo configurado.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R12-07 · Campo vacío

- **Requisito / criterio:** N-05
- **Precondición:** pantalla *Recuperar contraseña* con el campo vacío.
- **Pasos:**
  1. Pulsar *Enviar contraseña temporal*.
- **Resultado esperado:** no ocurre nada, no aparece ningún mensaje y no se envía nada.
- **Verificación previa:** 2026-11-25 · Correcto · con el campo vacío, *Enviar contraseña temporal* no hace nada y no aparece ningún mensaje.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R12-08 · Volver al inicio de sesión

- **Requisito / criterio:** CA-06
- **Precondición:** pantalla *Recuperar contraseña*.
- **Pasos:**
  1. Pulsar *Volver al inicio de sesión*.
- **Resultado esperado:** se abre *Iniciar sesión*.
- **Verificación previa:** 2026-11-25 · Correcto · *Volver al inicio de sesión* abre *Iniciar sesión*.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**
