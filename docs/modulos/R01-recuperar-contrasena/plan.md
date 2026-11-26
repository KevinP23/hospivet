# Plan — R1.2 Recuperar contraseña

| Campo | Valor |
|---|---|
| Estado | Cerrado (pruebas aprobadas por el responsable el 2026-11-25) |
| Versión | 1 |
| Fecha | 2026-11-24 |
| Requisitos | R1.2, R1.2.1–4 |
| Casos de uso y secuencias | 3.1.2 Recuperar contraseña (extend de Iniciar sesión) — SD-02 |
| Clases (sección 4.5) | Usuario (y Veterinario, que guarda el correo) |
| Tablas | `usuario` (`clave_hash`), `veterinario` (`correo`) |
| Depende de | R01 Autenticación (inicio de sesión), envío de correo SMTP de R07 (`HOSPIVET_MAIL_*`, N-09 de R07) |
| Fuente | `Iteracion5_Hospivet.pdf`, págs. 39 (R1.2), 53 (enlace en la pantalla de inicio de sesión), 56–60 (caso 3.1.2, actividad, interfaz y SD-02) y 239–240 (diccionario de `usuario` y `veterinario`) |

**Regla de este plan:** se implementa exactamente lo que define la Iteración 5 (requisitos, plantilla, interfaz y secuencia). No se agregan pantallas, campos, mensajes ni reglas, y no se omite ninguno.

**Indicación del docente (D-19):** lo que está en el PDF tiene que estar en las pantallas y no se puede agregar nada. En la sección 6 solo se proponen opciones que no agregan elementos a la interfaz.

## 1. Requisitos y caso de uso

| Ref. | Función | Categoría | Caso de uso |
|---|---|---|---|
| R1.2.1 | Solicitar recuperación de contraseña | Evidente | 3.1.2 |
| R1.2.2 | Verificar datos de identidad | Oculto | 3.1.2 |
| R1.2.3 | Mostrar mensaje "Usuario no registrado" si los datos no corresponden a ningún usuario | Evidente | 3.1.2 |
| R1.2.4 | Generar y enviar nueva contraseña | Oculto | 3.1.2 |

Flujo de 3.1.2:
1. El veterinario selecciona "Recuperar contraseña" desde la pantalla de inicio de sesión.
2. El sistema solicita los datos de verificación (usuario o correo registrado).
3. El veterinario los ingresa.
4. (4a) El sistema verifica que correspondan a un usuario registrado.
5. El sistema genera y envía una nueva contraseña temporal.

Curso alternativo 4b: los datos no corresponden a un usuario, el sistema muestra "Usuario no registrado" y se regresa al paso 3 sin guardar nada.

Precondición: el veterinario tiene un usuario o correo registrado. Postcondición: el sistema envía una contraseña temporal; si no verifica la identidad, no modifica las credenciales.

## 2. Criterios de aceptación

| ID | Origen | Criterio verificable | Mensaje del documento |
|---|---|---|---|
| CA-01 | 3.1.2 pasos 1–2 · enlace de la pantalla de inicio de sesión (pág. 53) | En *Iniciar sesión*, "Recuperar contraseña" abre la interfaz *Recuperar contraseña* con el título, el texto "Ingresa tu usuario o correo registrado", el campo *Usuario o correo registrado*, el botón *Enviar contraseña temporal* y el enlace *Volver al inicio de sesión*. | — |
| CA-02 | 3.1.2 curso 4b · SD-02 alt | Si lo escrito no corresponde a un usuario veterinario activo (ni por nombre de usuario ni por correo), aparece el mensaje bajo el campo, se conserva lo escrito y la contraseña de ningún usuario cambia. | "Usuario no registrado" |
| CA-03 | 3.1.2 pasos 4a–5 · SD-02 pasos 12–16 | Si corresponde a un usuario, el sistema genera una contraseña temporal, la envía al correo del veterinario y guarda su hash en `clave_hash`. La interfaz confirma la operación. | "Recuperación solicitada" (SD-02, pasos 14 y 16) |
| CA-04 | Postcondición 3.1.2 | Con la contraseña temporal recibida por correo se puede iniciar sesión, y la contraseña anterior deja de funcionar. | — |
| CA-05 | Postcondición 3.1.2 (no modificar credenciales si la operación no se completa) | Si el correo no se puede enviar, la contraseña anterior sigue funcionando. | Según N-03 |
| CA-06 | Interfaz Recuperar contraseña | *Volver al inicio de sesión* regresa a *Iniciar sesión*. | — |

## 3. Diseño

### 3.1 Datos

No hay tablas ni migraciones nuevas. Se actualiza `usuario.clave_hash` con el hash BCrypt de la contraseña temporal (diccionario 4.7.1: "No se almacena la contraseña en texto plano"). El correo de destino es `veterinario.correo` (4.7.2).

### 3.2 Backend — `AuthController` (AccesoController del documento)

| Secuencia | Operación del documento | Endpoint | Respuestas |
|---|---|---|---|
| SD-02 | `recuperarContrasena(identidad)` | `POST /api/auth/recuperacion` con `{ "identidad": "…" }` (público, como el inicio de sesión) | `200` "Recuperación solicitada" · `404` "Usuario no registrado" · error de correo según N-03 |

- **Búsqueda (R1.2.2, N-01):** usuario activo con rol `VETERINARIO` cuyo `nombre_usuario` sea igual a lo escrito, o cuyo `veterinario.correo` coincida sin distinguir mayúsculas.
- **Contraseña temporal (R1.2.4, N-02):** 8 caracteres (ajuste pedido por el responsable el 2026-11-25; antes 10) de letras y números, sin caracteres confusos (0/O, 1/l/I), generada con `SecureRandom`.
- **Orden de los pasos (CA-05):** en una sola transacción se guarda el nuevo hash y se envía el correo. Si el envío falla, la transacción se deshace y la contraseña anterior sigue vigente.
- **Correo:** se reutiliza la configuración SMTP de R07 (`HOSPIVET_MAIL_*`). Asunto "HOSPIVET · Contraseña temporal". El cuerpo lleva el nombre del veterinario, su usuario y la contraseña temporal. No es una pantalla, así que no cambia ninguna interfaz.
- La sesión no cambia: recuperar no inicia sesión.

Clases: método `recuperarContrasena` en `AuthService` (o un `RecuperacionService` en el paquete `auth`), DTO `RecuperacionRequest`, uso de `UsuarioMapper`, `VeterinarioMapper`, `PasswordEncoder` y `JavaMailSender`.

### 3.3 Frontend — interfaz *Recuperar contraseña*

- El texto "Recuperar contraseña" de *Iniciar sesión*, hoy solo visual, pasa a ser un enlace a la ruta pública `/recuperar-contrasena`.
- Vista `RecuperarContrasenaView.vue`, con el mismo marco que *Iniciar sesión* y exactamente los elementos del mockup:

| Elemento del mockup | Implementación |
|---|---|
| Logo HV, título "Recuperar contraseña" y texto "Ingresa tu usuario o correo registrado" | Igual que la tarjeta de inicio de sesión |
| Campo USUARIO O CORREO REGISTRADO | Campo de texto |
| Mensaje "Usuario no registrado" | Alerta roja bajo el campo, como en el mockup |
| Botón *Enviar contraseña temporal* | Llama a `POST /api/auth/recuperacion` |
| Enlace *Volver al inicio de sesión* | Lleva a `/login` |

## 4. Tareas

- [x] T1 Endpoint `POST /api/auth/recuperacion` con búsqueda por usuario o correo, contraseña temporal, envío de correo y transacción (SD-02).
- [x] T2 Vista *Recuperar contraseña*, ruta pública y enlace desde *Iniciar sesión*.
- [x] T3 Pruebas automatizadas de apoyo del backend: usuario inexistente, correo que falla sin cambiar la contraseña y recuperación correcta con un envío simulado.
- [x] T4 Registrar el correo del veterinario de esta computadora (hecho el 2026-11-25) y hacer la verificación previa de `pruebas.md` con un correo real (faltan CP-R12-03 a 05).
- [x] T5 `docs/trazabilidad`: requisitos implementados, decisión en `decisiones.md` y puntos para `actualizaciones-pendientes-documentos.md`. Actualizar el plan de R01 Autenticación (R1.2 deja de estar pendiente).

## 5. Lo que no se implementa

Todo lo que no figura en R1.2: pantalla para cambiar la contraseña, vencimiento de la contraseña temporal, enlaces de recuperación con token, preguntas de seguridad, recuperación para el rol `CLIENTE` (R10) y edición de los datos del veterinario.

## 6. Puntos que el documento no define

| ID | Qué falta en el documento | Opciones |
|---|---|---|
| N-01 | **Qué datos identifican al usuario.** El campo acepta "usuario o correo registrado", pero el documento no dice cómo se comparan ni a qué cuentas aplica. | **A)** Nombre de usuario exacto, o correo del veterinario sin distinguir mayúsculas. Solo cuentas activas con rol `VETERINARIO`, porque el acceso de clientes es R10. **B)** Solo el nombre de usuario. |
| N-02 | **Qué es "temporal".** No hay un caso de uso para cambiar la contraseña ni un mensaje de vencimiento, así que obligar a cambiarla o hacerla caducar agregaría pantallas o mensajes. | **A)** La contraseña temporal reemplaza a la anterior y sigue vigente hasta que se vuelva a recuperar. "Temporal" indica que es generada por el sistema, sin vencimiento. **B)** Vence en 24 horas; agregaría un mensaje que no está en el PDF. |
| N-03 | **Qué pasa si el correo no sale.** Puede faltar la configuración SMTP, no haber internet o no haber correo registrado para el usuario. El documento solo define "Usuario no registrado". | **A)** No se cambia la contraseña y se muestra la alerta general que ya usa la aplicación, "No fue posible completar la operación". Así la cuenta no queda bloqueada con una contraseña que nadie recibió. **B)** Se usa "Usuario no registrado" también en este caso; no agrega textos, pero no es exacto. |
| N-04 | **Cómo se muestra el resultado correcto.** El mockup solo muestra el error. SD-02 termina con "Recuperación solicitada". | **A)** Alerta verde con "Recuperación solicitada" (texto de SD-02, pasos 14 y 16) en el lugar del mensaje del mockup, y el campo se limpia. *Volver al inicio de sesión* sigue disponible. **B)** Volver solo a *Iniciar sesión* y mostrar allí "Recuperación solicitada". |
| N-05 | **Campo vacío.** El mockup no muestra un mensaje para el campo vacío. | **A)** *Enviar contraseña temporal* no hace nada mientras el campo esté vacío o con solo espacios; no se agrega ningún mensaje. **B)** Mostrar "Ingrese el usuario o correo" bajo el campo; agrega un texto que no está en el PDF. |

**Decisión del responsable (2026-11-24, D-20): N-01 A, N-02 A, N-03 A, N-04 A y N-05 A**, sin agregados en pantalla.

N-03 A usa la alerta general que la aplicación ya muestra ante cualquier error del servidor. N-04 A usa un texto que sí está en el PDF (SD-02).

## 7. Riesgos y requisitos previos

- **Correo del veterinario:** la cuenta `kpinto` de esta computadora se creó sin correo (no estaba en `HOSPIVET_BOOTSTRAP_CORREO`) y no existe una pantalla para editar veterinarios. Para probar R1.2, el responsable debe indicar el correo y se registra directamente en la base local. En una instalación nueva basta con definir `HOSPIVET_BOOTSTRAP_CORREO` antes del primer arranque.
- **Servidor de correo:** requiere las variables `HOSPIVET_MAIL_*` de R07, ya configuradas y probadas en esta computadora.
- **El mensaje revela si un usuario existe:** "Usuario no registrado" lo exige el documento (R1.2.3). Como HOSPIVET se instala solo en el equipo de la veterinaria (D-16), el riesgo es bajo.
- **Cualquiera puede pedir una contraseña temporal para otro usuario:** la contraseña nueva solo llega al correo registrado. El dueño de la cuenta la recibe y la anterior deja de servir; es el comportamiento que describe el caso de uso.
