# Plan — R02 Gestión de registro de cliente

| Campo | Valor |
|---|---|
| Estado | Implementado (retroactivo) · En revisión |
| Versión | 1 |
| Fecha | 2026-10-21 |
| Requisitos | R2.1, R2.1.1–6 |
| Casos de uso y secuencias | 3.1.3 Registrar cliente — SD-03 |
| Depende de | R01 (sesión de veterinario) |

## 1. Objetivo

Registrar al propietario de las mascotas con un CI válido y único para asociarlo después a mascotas, citas y facturas.

## 2. Alcance

**Incluye:** formulario de registro, validación local y del servidor, CI numérico y único, listado de clientes activos por API (usado por mascotas y citas).

**No incluye:** edición, baja ni listado visible de clientes (no figuran en la Iteración 5).

## 3. Criterios de aceptación

| ID | Requisito | Criterio verificable | Mensaje exacto |
|---|---|---|---|
| CA-01 | R2.1.1 | El formulario pide nombre, apellido, CI, teléfono, correo y dirección. | — |
| CA-02 | R2.1.2–3 | Si falta nombre, apellido, CI o teléfono, no se guarda y se conservan los datos. | "Complete los campos obligatorios" |
| CA-03 | R2.1.4–5 | Si el CI contiene caracteres no numéricos, no se guarda. | "CI inválido" |
| CA-04 | — | Si el CI ya existe, no se guarda. | "El CI ya está registrado" |
| CA-05 | R2.1.6 | Con datos válidos se guarda, se muestra confirmación y el formulario se limpia. | "Cliente registrado correctamente" |

## 4. Diseño implementado

### 4.1 Datos
Tabla `cliente` (V1): `ci` único, `nombre`, `apellido` y `telefono` obligatorios, `correo` y `direccion` opcionales, `activo`, `id_usuario` opcional reservado para R10. Coincide con el diccionario 4.7.3.

### 4.2 Backend
| Método | Ruta | Descripción | Errores |
|---|---|---|---|
| GET | `/api/clientes` | Clientes activos ordenados por apellido | 401/403 |
| POST | `/api/clientes` | Registra un cliente | 400 por campo, 409 CI duplicado |

Clases: `ClienteController`, `ClienteService`, `ClienteCreateRequest`, `ClienteResponse`, `Cliente`, `ClienteMapper`.

### 4.3 Frontend
`ClientesView.vue` (ruta `/clientes`), formulario de dos columnas según la interfaz de la Iteración 5.

## 5. Decisiones y diferencias con la documentación

| ID | Decisión | Motivo |
|---|---|---|
| D-03 | `apellido` obligatorio. | El modelo de datos lo declara obligatorio; el formulario de la Iteración 5 lo omitía. |
| D-08 | El formulario se limpia solo después de guardar. | No perder datos ante errores. |

## 6. Correcciones (aprobadas el 2026-10-21 · codificadas en COR-01 · pruebas de COR-01 aprobadas el 2026-10-22)

- [x] AUD-10: validar el formato del correo electrónico.
- [ ] AUD-O4: decidir si se agrega un listado o buscador para comprobar los clientes registrados. → mejora opcional OP-02, no incluida.

## 7. Riesgos y preguntas abiertas

- El teléfono no tiene formato definido en la documentación; hoy acepta cualquier texto de hasta 20 caracteres.
