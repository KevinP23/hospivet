# Plan — R09 Gestión de promociones

| Campo | Valor |
|---|---|
| Estado | Cerrado (pruebas aprobadas por el responsable el 2026-10-23) |
| Versión | 1 |
| Fecha | 2026-10-23 |
| Requisitos | R9.1–R9.4 y sus subfunciones |
| Casos de uso y secuencias | 3.1.26 Crear promoción — SD-26 · 3.1.27 Editar promoción — SD-27 · 3.1.28 Publicar promoción — SD-28 · 3.1.29 Eliminar promoción — SD-29 |
| Clases (sección 4.8) | Promocion, Producto, Servicio |
| Tablas (sección 4.8) | promocion, promocion_producto, promocion_servicio |
| Diccionario de datos | 4.7.15 `promocion` · 4.7.16 `promocion_producto` · 4.7.17 `promocion_servicio` |

**Regla de este plan:** se implementa exactamente lo que define la Iteración 5 (requisitos, plantillas, interfaces, secuencias y diccionario). No se agregan pantallas, campos, mensajes ni reglas, y no se omite ninguno. Lo que el documento no define se marca en la sección 6 para que el responsable lo resuelva.

## 1. Requisitos y casos de uso

| Ref. | Función | Categoría | Caso de uso |
|---|---|---|---|
| R9.1.1 | Ingresar datos de la promoción | Evidente | 3.1.26 |
| R9.1.2 | Validar que los campos obligatorios estén completos | Oculto | 3.1.26 |
| R9.1.3 | Mostrar mensaje "Complete los campos obligatorios" | Evidente | 3.1.26 |
| R9.1.4 | Validar que la fecha de fin de vigencia sea posterior a la de inicio | Oculto | 3.1.26 |
| R9.1.5 | Mostrar mensaje "La fecha de fin debe ser posterior a la de inicio" | Evidente | 3.1.26 |
| R9.1.6 | Guardar promoción | Oculto | 3.1.26 |
| R9.2.1 | Buscar promoción registrada | Evidente | 3.1.27 |
| R9.2.2 | Modificar datos de la promoción | Evidente | 3.1.27 |
| R9.2.3 | Validar título, descripción, descuento de 0 a 100 y fechas; el fin debe ser posterior al inicio. Verificar los productos o servicios seleccionados. | Oculto | 3.1.27 |
| R9.2.4 | Mostrar mensaje "Datos inválidos" si la validación falla | Evidente | 3.1.27 |
| R9.2.5 | Guardar promoción actualizada | Oculto | 3.1.27 |
| R9.3.1 | Seleccionar promoción a publicar | Evidente | 3.1.28 |
| R9.3.2 | Mostrar mensaje "La promoción ya está publicada" si corresponde | Evidente | 3.1.28 |
| R9.3.3 | Habilitar visibilidad para clientes | Oculto | 3.1.28 |
| R9.4.1 | Seleccionar promoción a eliminar | Evidente | 3.1.29 |
| R9.4.2 | Confirmar eliminación | Evidente | 3.1.29 |
| R9.4.3 | Mantener la promoción si el usuario no confirma | Evidente | 3.1.29 |
| R9.4.4 | Eliminar promoción del sistema | Oculto | 3.1.29 |

## 2. Criterios de aceptación

Cada criterio sale de la plantilla del caso de uso, de su interfaz y de su diagrama de secuencia. Los mensajes son los del documento.

| ID | Origen | Criterio verificable | Mensaje del documento |
|---|---|---|---|
| CA-01 | 3.1.26 curso 4b · SD-26 alt · interfaz alternativa | Si falta título, descripción, porcentaje o alguna fecha, no se guarda; se conserva el formulario y cada campo vacío muestra su aviso. | "Complete los campos obligatorios" · bajo el campo: "Campo obligatorio" |
| CA-02 | 3.1.26 curso 5b · SD-26 alt · interfaz | Si la fecha de fin no es posterior a la de inicio, no se guarda y se conservan los datos. | "La fecha de fin debe ser posterior a la de inicio" (alerta y bajo *Fecha de fin*) |
| CA-03 | 3.1.26 paso 6 · SD-26 | Con datos válidos se guarda la promoción con sus servicios o productos asociados, en estado *Borrador*. | "Promoción creada" (SD-26, paso 19) |
| CA-04 | 3.1.27 pasos 1–2 | Al buscar la promoción se muestran sus datos actuales. | — |
| CA-05 | 3.1.27 curso 4b · SD-27 alt | Título o descripción vacíos, descuento no numérico o fuera de 0 a 100, fechas incompletas o fin igual o anterior al inicio no se guardan; el sistema identifica el campo y explica la regla, y conserva la versión anterior. | "Datos inválidos" (ver N-03) |
| CA-06 | 3.1.27 paso 5 · SD-27 | Con datos válidos se guardan los cambios y sus asociaciones. | "Promoción actualizada" (SD-27, paso 16) |
| CA-07 | 3.1.28 pasos 1–2b · SD-28 | Al publicar una promoción no publicada, su estado pasa a *Publicada*. | "Promoción visible para clientes" (SD-28, paso 16) |
| CA-08 | 3.1.28 curso 2c · SD-28 alt · interfaz alternativa | Si ya está publicada, no se modifica y solo queda *Volver al listado*. | "La promoción ya está publicada" |
| CA-09 | 3.1.29 pasos 1–4 · SD-29 | Se muestran los datos de la promoción; al confirmar se elimina junto con sus asociaciones. | "Promoción eliminada" (SD-29, paso 16) |
| CA-10 | 3.1.29 curso 3b · SD-29 alt | Si no confirma, la promoción se mantiene y se vuelve al listado. | "Operación cancelada; la promoción se mantiene" |

## 3. Diseño

### 3.1 Datos — migración `V7__crear_promociones.sql`

Tablas y columnas exactamente como el diccionario 4.7.15 a 4.7.17 (NN = obligatorio):

| Tabla | Columnas |
|---|---|
| `promocion` | `id_promocion` PK · `titulo` NN (100) · `descripcion` NN (255) · `porcentaje_descuento` NN decimal (5,2), CHECK entre 0 y 100 · `fecha_inicio` NN · `fecha_fin` NN, CHECK > `fecha_inicio` · `estado` NN (20): `BORRADOR`, `PUBLICADA` o `FINALIZADA` |
| `promocion_producto` | `id_promocion` + `id_producto`: PK compuesta, FK→promocion y FK→producto |
| `promocion_servicio` | `id_promocion` + `id_servicio`: PK compuesta, FK→promocion y FK→servicio |

La factura (R07) no guarda una referencia a la promoción (diccionario 4.7.13), por lo que eliminar una promoción no afecta facturas.

### 3.2 Backend — `PromocionesController` y clase `Promocion`

| Secuencia | Operación del documento | Endpoint |
|---|---|---|
| SD-26 | `crear(datosPromocion)` | `POST /api/promociones` |
| SD-27 | `editar(idPromocion, datos)` | `PUT /api/promociones/{idPromocion}` |
| SD-28 | `publicar(idPromocion)` | `PATCH /api/promociones/{idPromocion}/publicacion` |
| SD-29 | `eliminar(idPromocion, confirmar)` | `DELETE /api/promociones/{idPromocion}` (la confirmación ocurre en la vista; sin confirmar no se llama al servidor) |

Consultas de apoyo: `GET /api/promociones` (listado de N-01 y campos *Buscar promoción* y *Seleccionar promoción*) y `GET /api/promociones/{idPromocion}` (datos actuales al editar). Los servicios y productos para *Aplicar a servicio o producto* salen de `/api/servicios` y `/api/inventario/productos`, que ya existen.

Paquete `com.hospivet.promocion`: modelos `Promocion`, `PromocionProducto`, `PromocionServicio`, sus mappers, DTO y servicio. `SecurityConfig` protege `/api/promociones/**` con el rol `VETERINARIO`. Código visible `PROM-00001`, derivado del identificador como en el mockup (`PROM-00124`).

### 3.3 Frontend — interfaces de la Iteración 5

Se habilita *Promociones* en la navegación (ya presente en la cabecera).

| Interfaz | Campos, textos y acciones (tal cual el mockup) |
|---|---|
| Crear promoción | Título; Descripción; Porcentaje de descuento; Aplicar a servicio o producto; Fecha de inicio; Fecha de fin; *Guardar promoción*; *Cancelar* |
| Editar promoción | Buscar promoción (`PROM-00124`); Título; Descripción; Porcentaje de descuento; Aplicar a servicio o producto; Fecha de inicio; Fecha de fin; *Buscar promoción*; *Guardar cambios*; *Cancelar*; texto "Título y descripción obligatorios; descuento numérico entre 0 y 100. Los productos o servicios seleccionados deben estar registrados." |
| Publicar promoción | "¿Publicar esta promoción para los clientes?"; Seleccionar promoción; Estado actual; Vigencia (`01/10/2026 a 31/10/2026`); *Sí, publicar promoción*; *Volver al listado*; texto "La promoción publicada será visible en el portal del cliente." |
| Eliminar promoción | "¿Eliminar esta promoción del sistema?"; Seleccionar promoción; Vigencia; *Sí, eliminar promoción*; *Cancelar*; texto "Si cancelas, la promoción se mantiene y vuelves al listado." |

Cada pantalla muestra sus referencias (R9.1, R9.1.1–6, etc.), la alerta roja y el mensaje bajo el campo como en los mockups, y conserva los datos ante un error.

## 4. Tareas

- [x] T1 Migración `V7__crear_promociones.sql` según el diccionario.
- [x] T2 Modelos, mappers, DTO, servicio y `PromocionesController` con las operaciones de SD-26 a SD-29.
- [x] T3 Vistas de las cuatro interfaces y habilitación de *Promociones* en la navegación.
- [x] T4 Pruebas automatizadas de apoyo del backend.
- [x] T5 `docs/trazabilidad`: requisitos implementados de R9.
- [x] T6 Verificación previa de `pruebas.md`.

## 5. Lo que no se implementa

Todo lo que no figura en R9.1–R9.4: la vista del cliente para ver promociones (R10.3) y la aplicación del descuento en la factura (R7.3.5) se harán en sus módulos.

## 6. Puntos que el documento no define

**Resueltos por el responsable el 2026-10-23: opción A en todos.**

| ID | Qué falta en el documento | Opciones |
|---|---|---|
| N-01 | **El listado de promociones.** *Publicar* y *Eliminar* tienen el botón *Volver al listado* ("vuelves al listado"), pero no hay interfaz de listado. | **A)** *Promociones* abre un listado con las columnas del diccionario (código, título, vigencia y estado) y acceso a *Crear*, *Editar*, *Publicar* y *Eliminar*; *Volver al listado* regresa a él. **B)** No hay listado: el módulo usa los cuatro botones de navegación (como Atención médica) y *Volver al listado* regresa a *Crear promoción*. |
| N-02 | **Uno o varios servicios o productos.** El mockup muestra un solo valor en *Aplicar a servicio o producto*, pero el diccionario permite varios (tablas N:M). El campo no figura entre los obligatorios. | **A)** Selector de varios servicios y productos, opcional (como el diccionario). **B)** Selector de uno solo, opcional (como el mockup). |
| N-03 | **Mensaje de *Editar promoción*.** R9.2.4 y SD-27 dicen "Datos inválidos" y piden identificar el campo y explicar la regla; el mockup muestra en la alerta "Vigencia inválida: la fecha de fin debe ser posterior al inicio." | **A)** Alerta "Datos inválidos" y, bajo el campo, la regla con los textos del mockup ("Campo obligatorio", "Fin posterior al inicio", "Descuento numérico entre 0 y 100"). **B)** Alerta con el texto del mockup según la regla incumplida. |
| N-04 | **Descuento fuera de 0 a 100 al crear.** El diccionario lo exige, pero *Crear promoción* solo valida obligatorios y fechas, sin mensaje para el descuento. | **A)** El campo solo acepta números de 0 a 100 (control numérico con mínimo y máximo), sin mensaje nuevo, y la base lo impide. **B)** Otra forma que indiques. |
| N-05 | **Estado `FINALIZADA`.** Existe en el diccionario, pero ningún requisito lo asigna. | **A)** No se usa en este módulo; quedan `BORRADOR` (al crear) y `PUBLICADA` (al publicar). **B)** Se asigna cuando la fecha de fin ya pasó. |
| N-06 | **Cómo se busca la promoción en *Editar*.** El mockup muestra el código `PROM-00124` y el botón *Buscar promoción*, pero no hay mensaje para un código que no existe. | **A)** *Buscar promoción* es un selector que filtra por código o título entre las promociones registradas (no hay caso "no encontrada"). **B)** Campo de texto para escribir el código, con el mensaje que indiques si no existe. |
