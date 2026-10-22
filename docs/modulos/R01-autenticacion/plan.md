# Plan — R01 Autenticación y acceso

| Campo | Valor |
|---|---|
| Estado | Implementado (retroactivo) · En revisión — R1.1 · Sin plan — R1.2 |
| Versión | 1 |
| Fecha | 2026-10-21 |
| Requisitos | R1.1, R1.1.1–6 (implementado) · R1.2, R1.2.1–4 (pendiente) |
| Casos de uso y secuencias | 3.1.1 Iniciar sesión — SD-01 · 3.1.2 Recuperar contraseña — SD-02 |
| Depende de | Tablas `usuario` y `veterinario` |

## 1. Objetivo

Permitir que el veterinario ingrese al sistema con usuario y contraseña, y mantener su sesión activa mientras usa los módulos.

## 2. Alcance

**Incluye:** inicio de sesión del veterinario, sesión HTTP, consulta de la sesión actual, cierre de sesión, protección de rutas y creación única de la cuenta veterinaria inicial por variables de entorno.

**No incluye:** recuperar contraseña (R1.2), acceso del rol `CLIENTE` (R10) y alta de usuarios desde la interfaz.

## 3. Criterios de aceptación

| ID | Requisito | Criterio verificable | Mensaje exacto |
|---|---|---|---|
| CA-01 | R1.1.1 | El formulario solicita usuario y contraseña; ambos son obligatorios. | "Ingrese el usuario" / "Ingrese la contraseña" |
| CA-02 | R1.1.2–3 | Con un usuario inexistente o inactivo no se inicia sesión. | "Usuario no encontrado" |
| CA-03 | R1.1.4–5 | Con contraseña incorrecta no se inicia sesión y se conservan los datos del formulario. | "Contraseña incorrecta" |
| CA-04 | R1.1.6 | Con credenciales correctas se abre el menú principal (módulo Clientes). | — |
| CA-05 | — | Sin sesión, cualquier ruta interna redirige al inicio de sesión. | — |
| CA-06 | — | *Cerrar sesión* termina la sesión y vuelve al inicio de sesión. | — |

## 4. Diseño implementado

### 4.1 Datos
Migración `V1__crear_usuarios_clientes_mascotas.sql`: `usuario` (credenciales, `clave_hash` BCrypt, rol `VETERINARIO`/`CLIENTE`, `activo`) y `veterinario` (clave compartida con `usuario`). Coincide con el diccionario 4.7.1 y 4.7.2.

### 4.2 Backend
| Método | Ruta | Descripción | Errores |
|---|---|---|---|
| GET | `/api/auth/csrf` | Entrega el token CSRF | — |
| POST | `/api/auth/login` | Valida credenciales y crea la sesión | 400 campos, 401 usuario/contraseña, 403 rol |
| GET | `/api/auth/me` | Devuelve el usuario de la sesión | 401 |
| POST | `/api/auth/logout` | Cierra la sesión | — |

Clases: `AuthController`, `AuthService`, `LoginRequest`, `AuthResponse`, `Usuario`, `UsuarioMapper`, `SecurityConfig`, `InitialVeterinarianBootstrap`.

### 4.3 Frontend
`LoginView.vue` (ruta `/login`), `api/auth.js`, guardia de rutas en `router/index.js`, menú de perfil con *Cerrar sesión* en `AppHeader.vue`. El enlace *Recuperar contraseña* es solo visual.

## 5. Decisiones y diferencias con la documentación

| ID | Decisión | Motivo |
|---|---|---|
| D-04 | Solo inicia sesión el rol `VETERINARIO`. | El cliente se habilita en R10. |
| D-05 | Cuenta inicial creada una sola vez por variables de entorno. | No guardar credenciales en el repositorio. |
| D-06 | Sesión HTTP con contraseña BCrypt y protección CSRF. | Seguridad básica sin almacenar contraseñas en texto plano. |

## 6. Correcciones (aprobadas el 2026-10-21 · codificadas en COR-01 · pruebas de COR-01 aprobadas el 2026-10-22)

- [x] AUD-12: verificar la contraseña antes de informar que la cuenta no es de veterinario.
- [x] AUD-06: al expirar la sesión, volver automáticamente al inicio de sesión.
- [x] AUD-13: quitar el usuario en memoria que Spring genera por defecto.
- [x] AUD-11: mensajes en español para JSON mal formado, parámetros inválidos y errores inesperados.

## 7. Riesgos y preguntas abiertas

- R1.2 necesita definir cómo se enviará la contraseña temporal (servidor de correo); se planificará como módulo propio.
