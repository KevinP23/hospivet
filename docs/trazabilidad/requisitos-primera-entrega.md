# Requisitos trazados de la primera entrega

Fuente principal: `Iteracion5_Hospivet_integrada.docx`. Contraste de modelo: `Iteracion4_Hospivet_modelos_corregidos.docx`.

| Requisito / diseño | Actor | Implementación | Validaciones y flujos alternativos | Estado |
|---|---|---|---|---|
| R1.1, R1.1.1–R1.1.6, SD-01: iniciar sesión | Veterinario | `POST /api/auth/login`, sesión HTTP, `LoginView.vue` | Usuario y contraseña obligatorios; “Usuario no encontrado”; “Contraseña incorrecta”; rol distinto de veterinario rechazado; no se crea sesión al fallar. | Implementado |
| R2.1, R2.1.1–R2.1.6, SD-03: registrar cliente | Veterinario autenticado | `POST /api/clientes`, tabla `cliente`, `ClientesView.vue` | CI, nombre, apellido y teléfono obligatorios; CI solo numérico y único; límites de longitud; ante error no se inserta y el formulario conserva los datos. | Implementado |
| R3.1, R3.1.1–R3.1.7, SD-04: registrar mascota | Veterinario autenticado | `POST /api/mascotas`, tabla `mascota`, `MascotasView.vue` | Propietario existente y activo; nombre, especie y peso obligatorios; peso mayor que cero; fecha no futura; límites de longitud; ante error no se inserta y el formulario conserva los datos. | Implementado |

## Campos implementados

### Inicio de sesión

| Campo | Regla |
|---|---|
| Usuario | Obligatorio, máximo 50 caracteres. |
| Contraseña | Obligatoria. Se compara con hash BCrypt. |

### Cliente

| Campo | Regla |
|---|---|
| CI | Obligatorio, numérico, único, máximo 15 caracteres. |
| Nombre | Obligatorio, máximo 60 caracteres. |
| Apellido | Obligatorio, máximo 60 caracteres. |
| Teléfono | Obligatorio, máximo 20 caracteres. |
| Correo | Opcional, máximo 100 caracteres. |
| Dirección | Opcional, máximo 150 caracteres. |

### Mascota

| Campo | Regla |
|---|---|
| Cliente propietario | Obligatorio; debe existir y estar activo. Relación N:1 desde mascota hacia cliente. |
| Nombre | Obligatorio, máximo 60 caracteres. |
| Especie | Obligatoria, máximo 40 caracteres. |
| Raza | Opcional, máximo 60 caracteres. |
| Fecha de nacimiento | Opcional mientras los documentos no definan expresamente su obligatoriedad; no puede ser futura. La edad se calcula. |
| Peso | Obligatorio y mayor que cero. |
| Sexo | Opcional, un carácter. |
| Observaciones | Opcional, máximo 255 caracteres. |

## Permisos

- `/api/auth/login`, `/api/auth/csrf` y `/api/health` son públicos.
- `/api/auth/me` y el cierre de sesión requieren autenticación.
- `/api/clientes/**` y `/api/mascotas/**` requieren el rol `VETERINARIO`.
- La protección CSRF se mantiene en operaciones autenticadas que modifican datos.
