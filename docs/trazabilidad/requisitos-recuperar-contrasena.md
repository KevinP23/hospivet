# Trazabilidad de Recuperar contraseña

Fuente: Iteración 5 (`Iteracion5_Hospivet.pdf`), caso 3.1.2 Recuperar contraseña (R1.2), su interfaz y SD-02. Los documentos originales no se modifican. Plan y pruebas: `docs/modulos/R01-recuperar-contrasena`.

Indicación del docente: las pantallas deben contener exactamente lo que muestra el PDF, sin agregados.

## Requisitos implementados

| Requisito | Caso de uso / secuencia | Implementación |
|---|---|---|
| R1.2.1 | 3.1.2 paso 1 | El texto "Recuperar contraseña" de *Iniciar sesión* abre la ruta pública `/recuperar-contrasena`. |
| R1.2.2 | 3.1.2 paso 4a · SD-02 `recuperarContrasena(identidad)` | `POST /api/auth/recuperacion`: busca un veterinario activo por nombre de usuario exacto o por correo sin distinguir mayúsculas. |
| R1.2.3 | 3.1.2 curso 4b · SD-02 alt | Mensaje "Usuario no registrado" bajo el campo; no se modifica ninguna credencial. |
| R1.2.4 | 3.1.2 paso 5 · SD-02 pasos 12–16 | Genera una contraseña temporal de 10 caracteres, guarda su hash BCrypt en `usuario.clave_hash`, la envía al correo del veterinario y responde "Recuperación solicitada". |

## Correspondencia con el modelo de datos

No hay tablas ni migraciones nuevas. Se actualiza `usuario.clave_hash` (4.7.1, nunca en texto plano) y el correo de destino es `veterinario.correo` (4.7.2).

## Puntos que el documento no definía (resueltos por el responsable el 2026-11-24, D-20)

| ID | Decisión |
|---|---|
| N-01 | Se identifica por nombre de usuario exacto o por correo sin distinguir mayúsculas; solo cuentas activas con rol `VETERINARIO`. |
| N-02 | La contraseña temporal reemplaza a la anterior y no vence; no hay pantalla para cambiarla. |
| N-03 | Si el correo no se puede enviar (sin correo registrado, sin SMTP o sin internet), la transacción se deshace, la contraseña anterior sigue vigente y se muestra la alerta general "No fue posible completar la operación". |
| N-04 | El resultado correcto se muestra en verde con "Recuperación solicitada" (SD-02) y el campo se limpia. |
| N-05 | Con el campo vacío, *Enviar contraseña temporal* no hace nada y no se muestra ningún mensaje. |

## Correspondencia técnica

- Paquete `com.hospivet.auth`: `RecuperacionService`, DTO `RecuperacionRequest` y `RecuperacionResponse`, y el endpoint en `AuthController` (AccesoController del documento). Reutiliza la configuración SMTP de R07 (`HOSPIVET_MAIL_*`).
- Seguridad: `/api/auth/recuperacion` es público, como el inicio de sesión, y exige el token CSRF.
- Interfaz: vista `RecuperarContrasenaView.vue`, enlace en `LoginView.vue` y *Menú principal* marcado en la cabecera, como en el mockup.
- Correo: asunto "HOSPIVET · Contraseña temporal", con el nombre del veterinario, su usuario y la contraseña temporal.
