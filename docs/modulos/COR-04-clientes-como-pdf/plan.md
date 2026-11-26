# Plan — COR-04 Pantalla Registrar cliente igual al PDF

| Campo | Valor |
|---|---|
| Estado | Cerrado (pruebas aprobadas por el responsable el 2026-11-26) |
| Versión | 1 |
| Fecha | 2026-11-26 |
| Origen | Revisión de R02 del 2026-11-26: diferencias D1 a D5 entre *Registrar cliente* y su mockup |
| Requisitos | R2.1, R2.1.1–6 (sin cambios de funcionamiento) |
| Fuente | `Iteracion5_Hospivet.pdf`, págs. 64 (Interfaz Registrar cliente), 65 (estado "campos obligatorios incompletos") y 66 (SD-03) |
| Módulos afectados | R02 Clientes (solo la pantalla) |

**Regla de este plan:** indicación del docente (D-19): lo que está en el PDF tiene que estar en las pantallas y no se puede agregar nada.

## 1. Diferencias y decisión del responsable

| ID | Hoy en la aplicación | En el PDF | Decisión |
|---|---|---|---|
| D1 | Asteriscos rojos en Nombre, Apellido, Carnet de identidad y Teléfono. | Sin asteriscos. | Quitarlos. |
| D2 | Bajo cada campo obligatorio vacío: "Complete los campos obligatorios". | Bajo el campo: "Campo obligatorio"; el texto largo solo en la alerta (pág. 65). | Usar "Campo obligatorio" bajo el campo. |
| D3 | Aviso al guardar: "Cliente registrado correctamente". | SD-03, pasos 14 y 16: "Cliente registrado". | Usar "Cliente registrado". |
| D4 | "Correo inválido" si el correo está mal escrito. | No está. | **Se mantiene**: evita fallos posteriores del envío de facturas y de la recuperación de contraseña. |
| D5 | "El CI ya está registrado" si el CI se repite. | No está; el diccionario exige CI único y SD-03 "comprobar CI". | **Se mantiene**. |

## 2. Criterios de aceptación

| ID | Criterio verificable |
|---|---|
| CA-01 | Los campos obligatorios no muestran asterisco. |
| CA-02 | Con campos obligatorios vacíos: alerta "Complete los campos obligatorios" y "Campo obligatorio" bajo cada campo vacío, como en la pág. 65. |
| CA-03 | Al guardar correctamente aparece "Cliente registrado" y el formulario se limpia. |
| CA-04 | "CI inválido", "Correo inválido" y "El CI ya está registrado" siguen funcionando igual. |

## 3. Cambios

- `frontend/src/views/clientes/ClientesView.vue`: `hide-required-asterisk` (D1); mensaje "Campo obligatorio" en las reglas de obligatorios (D2); aviso "Cliente registrado" (D3).
- Sin cambios en el backend ni en la base de datos.
- Documentación: CA-05 de R02 y casos CP-R02-02 y CP-R02-05; decisión D-23; comparación con las interfaces.

## 4. Tareas

- [x] T1 Cambios en `ClientesView.vue`.
- [x] T2 Verificar CA-01 a CA-04 en la aplicación.
- [x] T3 Actualizar R02 (plan y pruebas), `decisiones.md` y `comparacion-interfaces-iteracion5.md`.
