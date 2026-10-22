# Plan — R03 Registrar mascota

| Campo | Valor |
|---|---|
| Estado | Implementado (retroactivo) · En revisión |
| Versión | 1 |
| Fecha | 2026-10-21 |
| Requisitos | R3.1, R3.1.1–7 |
| Casos de uso y secuencias | 3.1.4 Registrar mascota — SD-04 |
| Depende de | R02 (cliente activo) |

## 1. Objetivo

Registrar la mascota y vincularla obligatoriamente a un cliente activo para llevar su historial.

## 2. Alcance

**Incluye:** formulario con propietario, nombre, especie, raza, fecha de nacimiento, edad calculada, peso, sexo y observaciones; listado de mascotas activas por API (usado por citas).

**No incluye:** edición, baja ni listado visible de mascotas.

## 3. Criterios de aceptación

| ID | Requisito | Criterio verificable | Mensaje exacto |
|---|---|---|---|
| CA-01 | R3.1.1 | El formulario muestra los campos y la edad se calcula al elegir la fecha de nacimiento. | — |
| CA-02 | R3.1.2–3 | Sin propietario, nombre, especie o peso no se guarda y se conservan los datos. | "Complete los campos obligatorios" |
| CA-03 | R3.1.4–5 | Peso cero o negativo, o fecha de nacimiento futura, no se guarda. | "Edad o peso inválido" |
| CA-04 | R3.1.6–7 | Con datos válidos se guarda vinculada al propietario y el formulario se limpia. | "Mascota registrada y vinculada al propietario" |

## 4. Diseño implementado

### 4.1 Datos
Tabla `mascota` (V1): `id_cliente` obligatorio (FK), `fecha_nacimiento` opcional, `peso NUMERIC(6,2) > 0`, `sexo` de un carácter, `activo`. Coincide con el diccionario 4.7.4.

### 4.2 Backend
| Método | Ruta | Descripción | Errores |
|---|---|---|---|
| GET | `/api/mascotas?clienteId=` | Mascotas activas, opcionalmente de un cliente | 401/403 |
| POST | `/api/mascotas` | Registra una mascota | 400 por campo, 404 propietario inexistente o inactivo |

Clases: `MascotaController`, `MascotaService`, `MascotaCreateRequest`, `MascotaResponse` (calcula la edad), `Mascota`, `MascotaMapper`.

### 4.3 Frontend
`MascotasView.vue` (ruta `/mascotas`): selector de propietario con nombre y CI, calendario sin fechas futuras, edad de solo lectura, sexo Macho/Hembra.

## 5. Decisiones y diferencias con la documentación

| ID | Decisión | Motivo |
|---|---|---|
| D-02 | Se guarda `fecha_nacimiento`; la edad se calcula. | La edad cambia con el tiempo. |
| — | La fecha de nacimiento es opcional. | La documentación no define su obligatoriedad. |

## 6. Correcciones (aprobadas el 2026-10-21 · codificadas en COR-01 · pruebas de COR-01 aprobadas el 2026-10-22)

- [x] AUD-02: calcular la fecha de hoy en hora local (hoy usa UTC y entre 20:00 y 23:59 acepta la fecha de mañana).
- [x] AUD-10: limitar el peso a 4 enteros y 2 decimales y aceptar solo `M` o `H` como sexo en el servidor.
- [x] AUD-O4: mostrar la edad en meses cuando la mascota tiene menos de un año (hoy muestra "0 años").

## 7. Riesgos y preguntas abiertas

- El diccionario 4.7.4 menciona transformar una edad aproximada en fecha estimada; hoy solo se ingresa la fecha. ¿Se requiere ingresar edad aproximada?
