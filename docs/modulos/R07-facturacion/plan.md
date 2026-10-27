# Plan — R07 Gestión de facturación

| Campo | Valor |
|---|---|
| Estado | Cerrado (pruebas aprobadas por el responsable el 2026-10-27) |
| Versión | 2 (decisiones N-01 a N-11 y contexto de instalación local) |
| Fecha | 2026-10-27 |
| Requisitos | R7.1–R7.5 y sus subfunciones |
| Casos de uso y secuencias | 3.1.17 Revisar factura — SD-17 · 3.1.18 Editar factura — SD-18 · 3.1.19 Generar factura — SD-19 · 3.1.20 Imprimir factura — SD-20 · 3.1.21 Enviar factura — SD-21 |
| Clases (sección 4.8) | Factura, Cliente, Producto, Servicio, Promocion |
| Tablas (sección 4.8) | factura, detalle_factura, cliente, producto, servicio, promocion |
| Diccionario de datos | 4.7.13 `factura` · 4.7.14 `detalle_factura` |

**Contexto de instalación (D-16):** HOSPIVET se instala en la computadora de la veterinaria (servidor, base de datos e interfaz en el mismo equipo), no como sitio web publicado. La impresora está conectada a ese equipo y el correo sale desde él.

**Regla de este plan:** se implementa exactamente lo que define la Iteración 5 (requisitos, plantillas, interfaces, secuencias y diccionario). No se agregan pantallas, campos, mensajes ni reglas, y no se omite ninguno. Lo que el documento no define se marca en la sección 6 para que el responsable lo resuelva.

## 1. Requisitos y casos de uso

| Ref. | Función | Categoría | Caso de uso |
|---|---|---|---|
| R7.1.1 | Buscar factura registrada | Evidente | 3.1.17 |
| R7.1.2 | Mostrar mensaje "Factura no encontrada" si no existe el registro | Evidente | 3.1.17 |
| R7.1.3 | Mostrar detalle de la factura | Evidente | 3.1.17 |
| R7.2.1 | Buscar factura a editar | Evidente | 3.1.18 |
| R7.2.2 | Modificar datos de la factura | Evidente | 3.1.18 |
| R7.2.3 | Validar cliente, fecha y detalle; cantidad mayor que 0, precio no negativo y descuento de 0 al subtotal. Identificar el campo inválido y permitir corregirlo. | Oculto | 3.1.18 |
| R7.2.4 | Mostrar mensaje "Datos inválidos" si la validación falla | Evidente | 3.1.18 |
| R7.2.5 | Guardar cambios | Oculto | 3.1.18 |
| R7.3.1 | Obtener datos del cliente | Oculto | 3.1.19 |
| R7.3.2 | Obtener servicio o producto facturado | Oculto | 3.1.19 |
| R7.3.3 | Mostrar mensaje "Servicio o producto no encontrado" y cancelar la operación | Evidente | 3.1.19 |
| R7.3.4 | Sumar subtotales del detalle | Oculto | 3.1.19 |
| R7.3.5 | Aplicar descuento de promoción vigente si corresponde | Oculto | 3.1.19 |
| R7.3.6 | Guardar factura generada | Oculto | 3.1.19 |
| R7.4.1 | Seleccionar factura a imprimir | Evidente | 3.1.20 |
| R7.4.2 | Generar formato de impresión | Oculto | 3.1.20 |
| R7.4.3 | Mostrar mensaje "Error de impresión, verifique la impresora" si falla el envío a impresora | Evidente | 3.1.20 |
| R7.5.1 | Seleccionar medio de envío | Evidente | 3.1.21 |
| R7.5.2 | Mostrar mensaje "El cliente no tiene correo registrado" si falta el dato | Evidente | 3.1.21 |
| R7.5.3 | Enviar factura al cliente | Oculto | 3.1.21 |

## 2. Criterios de aceptación

| ID | Origen | Criterio verificable | Mensaje del documento |
|---|---|---|---|
| CA-01 | 3.1.17 pasos 1–2b · SD-17 | Al buscar una factura registrada se muestran cliente, fecha de emisión, estado, detalle (descripción, cantidad, precio unitario, subtotal), subtotal, descuento y total. | "Detalle de factura" (SD-17, paso 14) |
| CA-02 | 3.1.17 curso 2c · SD-17 alt | Si la factura no existe, se conserva el criterio de búsqueda y no se muestran filas ni totales. | "Factura no encontrada" |
| CA-03 | 3.1.19 pasos 1–2b · SD-19 | Al generar, el sistema obtiene el cliente y los servicios o productos agregados al detalle. | — |
| CA-04 | 3.1.19 curso 2c · SD-19 alt | Si un servicio o producto no se encuentra, la operación se cancela y no se muestran filas ni totales ficticios. | "Servicio o producto no encontrado" |
| CA-05 | 3.1.19 pasos 3–6 · SD-19 | Al confirmar, suma los subtotales, aplica el descuento de la promoción vigente si corresponde, calcula el total (subtotal − descuento) y guarda cabecera y detalle en una sola transacción. | "Factura generada con total calculado" (SD-19, paso 16) |
| CA-06 | 3.1.18 pasos 1–2 | Al buscar la factura a editar se muestran sus datos actuales. | — |
| CA-07 | 3.1.18 curso 4b · SD-18 alt | Cliente no seleccionado o inexistente, fecha incorrecta, detalle sin descripción o sin producto/servicio válido, cantidad no numérica o ≤ 0, precio no numérico o negativo, o descuento fuera de 0 a subtotal: no se guarda, se identifica el campo y se explica la regla. | "Datos inválidos" |
| CA-08 | 3.1.18 pasos 4a–5 · SD-18 | Con datos válidos se recalculan subtotales y total y se guardan cabecera y detalle en una sola transacción. | "Factura actualizada" (SD-18, paso 16) |
| CA-09 | 3.1.20 pasos 1–2b · SD-20 | Al seleccionar la factura se genera su formato de impresión (vista previa) y se envía a imprimir. | "Documento preparado para impresión" (SD-20, paso 15) |
| CA-10 | 3.1.20 curso 2c · SD-20 alt | Si falla el envío a la impresora, se conserva la vista previa y se puede reintentar. | "Error de impresión, verifique la impresora" |
| CA-11 | 3.1.21 curso 2c · SD-21 alt | Si el cliente no tiene correo, no se envía la factura. | "El cliente no tiene correo registrado" |
| CA-12 | 3.1.21 pasos 1–2b · SD-21 | Con correo registrado, la factura se envía por el medio seleccionado (correo electrónico). | "Solicitud de envío confirmada por proveedor" (SD-21, paso 15) |

## 3. Diseño

### 3.1 Datos — migración `V8__crear_facturacion.sql`

Tablas y columnas exactamente como el diccionario 4.7.13 y 4.7.14 (NN = obligatorio):

| Tabla | Columnas |
|---|---|
| `factura` | `id_factura` PK · `id_cliente` NN FK→cliente · `id_veterinario` NN FK→veterinario · `fecha_hora` NN · `subtotal` NN (12,2) ≥ 0 · `descuento` NN (12,2), 0 ≤ descuento ≤ subtotal · `total` NN (12,2) ≥ 0, = subtotal − descuento · `estado` NN (20): `EMITIDA`, `ANULADA` o `PAGADA` |
| `detalle_factura` | `id_detalle` PK · `id_factura` NN FK→factura · `id_producto` FK opcional · `id_servicio` FK opcional · `descripcion` NN (150), texto congelado del ítem · `cantidad` NN > 0 · `precio_unitario` NN (10,2) ≥ 0 · `subtotal` NN (12,2) = cantidad × precio · `tipo_item` NN: `PRODUCTO` o `SERVICIO`, con exactamente una de las dos claves |

Regla del diccionario: toda factura emitida tiene al menos un detalle (se valida antes de guardar).

### 3.2 Backend — `FacturacionController` y clase `Factura`

| Secuencia | Operación del documento | Endpoint |
|---|---|---|
| SD-17 | `consultarDetalle(idFactura)` | `GET /api/facturas/{idFactura}` |
| SD-18 | `editar(idFactura, datos, items)` | `PUT /api/facturas/{idFactura}` |
| SD-19 | `generar(cliente, items)` | `POST /api/facturas` |
| SD-20 | `imprimir(idFactura)` | `POST /api/facturas/{idFactura}/impresion` (impresión directa en la impresora del equipo, N-08 B); `GET /api/impresoras` lista las impresoras instaladas |
| SD-21 | `enviar(idFactura, medio)` | `POST /api/facturas/{idFactura}/envio` (según N-09) |

Paquete `com.hospivet.factura`: modelos `Factura` y `DetalleFactura` (entidad relacional; no se agrega como clase de dominio, igual que en el diagrama de clases), mappers, DTO, servicio y controlador. Número visible de 6 dígitos (`000124`), derivado del identificador como en los mockups. `id_veterinario` sale de la sesión.

### 3.3 Frontend — interfaces de la Iteración 5

Se habilita *Facturación* en la navegación.

| Interfaz | Campos, textos y acciones (tal cual el mockup) |
|---|---|
| Revisar factura | Buscar factura registrada; Cliente; Fecha de emisión; Estado; *Buscar factura*; *Volver al listado*; tabla Descripción · Cantidad · Precio unitario · Subtotal; Subtotal, Descuento y Total |
| Editar factura | Buscar factura a editar; Cliente; Fecha; Descripción del detalle; Cantidad; Precio unitario en Bs; Descuento en Bs; *Buscar factura*; *Guardar cambios*; *Cancelar*; texto "Cliente y descripción obligatorios; fecha válida; precio no negativo. Descuento entre 0 y el subtotal. El sistema recalcula el total." |
| Generar factura | Cliente; Agregar servicio o producto; Promoción vigente; *Agregar al detalle*; *Confirmar y generar factura*; *Cancelar*; tabla Descripción · Cantidad · Precio unitario · Subtotal; Subtotal, Descuento y Total |
| Imprimir factura | Seleccionar factura; Impresora; *Vista previa*; *Imprimir*; *Reintentar*; tabla del detalle; Total a pagar |
| Enviar factura | Factura seleccionada; Cliente; Medio de envío (Correo electrónico); Correo del cliente; *Enviar factura*; *Actualizar correo*; *Cancelar* |

Importes en bolivianos con el formato de los mockups (`Bs 80,00`).

## 4. Tareas

- [x] T1 Migración `V8__crear_facturacion.sql` según el diccionario.
- [x] T2 Modelos, mappers, DTO, servicio y `FacturacionController` con las operaciones de SD-17 a SD-21.
- [x] T3 Vistas de las cinco interfaces y habilitación de *Facturación* en la navegación.
- [x] T4 Pruebas automatizadas de apoyo del backend.
- [x] T5 `docs/trazabilidad`: requisitos implementados de R7.
- [x] T6 Verificación previa de `pruebas.md`.

## 5. Lo que no se implementa

Todo lo que no figura en R7.1–R7.5: anular o marcar como pagada una factura, pagos en línea (limitación 2.5), facturación electrónica ante impuestos y reportes de ingresos (R6.2, que se hará en su módulo).

## 6. Puntos que el documento no define

**Decisiones del responsable (2026-10-27): opción A en N-01 a N-07 y N-09 a N-11; opción B en N-08** (impresión directa por la instalación local, sección 7). N-03: por ahora todos los precios son de prueba; *Consulta general* pasa a **Bs 80,00**, el valor del mockup de *Generar factura*, y el responsable lo cambiará por el precio real más adelante.

| ID | Qué falta en el documento | Opciones |
|---|---|---|
| N-01 | **Listado de facturas.** *Revisar factura* tiene *Volver al listado*, pero no hay interfaz de listado. | **A)** *Facturación* abre un listado (número, cliente, fecha, total y estado) con acceso a las cinco operaciones, igual que en Promociones. **B)** Sin listado: botones de navegación entre las cinco pantallas. |
| N-02 | **Cantidad y precio al generar.** *Generar factura* solo tiene *Agregar servicio o producto* y *Agregar al detalle*; la tabla muestra cantidad y precio unitario sin campos para ingresarlos. | **A)** Cada vez que se agrega el mismo ítem la cantidad sube en 1; el precio unitario es el del catálogo (servicio o producto). Cantidad y precio se corrigen después con *Editar factura*. **B)** Agregar campos de cantidad y precio en *Generar factura*. |
| N-03 | **Precio de *Consulta general*.** Quedó en `0.00 Bs` como valor provisional (decisión de R05); una factura con ese servicio saldría en cero. | **A)** Indicas el precio real y se actualiza con una migración. **B)** Se mantiene `0.00 Bs` hasta que se defina. |
| N-04 | **Cómo se aplica la promoción vigente.** R7.3.5 dice "si corresponde" y el mockup muestra *Promoción vigente* con un solo valor. | **A)** El campo ofrece las promociones *Publicadas* cuya vigencia incluye la fecha de emisión y que estén asociadas a algún ítem del detalle; el descuento es el porcentaje sobre los subtotales de los ítems asociados a esa promoción (si la promoción no tiene ítems asociados, sobre todo el subtotal). **B)** El porcentaje se aplica siempre sobre todo el subtotal. |
| N-05 | **Estados `ANULADA` y `PAGADA`.** Existen en el diccionario, pero ningún requisito los asigna. | **A)** Solo se usa `EMITIDA` (al generar). **B)** Otra forma que indiques. |
| N-06 | **Edición de varias líneas.** El mockup de *Editar factura* muestra los campos de una sola línea (descripción, cantidad, precio), pero una factura puede tener varias. | **A)** Un selector elige la línea del detalle y los campos del mockup editan esa línea; se pueden corregir varias líneas antes de guardar. **B)** Todas las líneas editables a la vez en una tabla. |
| N-07 | **Mensaje de *Editar factura*.** R7.2.4 y SD-18 dicen "Datos inválidos"; el mockup muestra "Cantidad inválida: debe ser numérica y mayor que 0." | **A)** Igual que en Promociones: alerta "Datos inválidos" y la regla bajo el campo con los textos del mockup ("Cantidad mayor que 0", etc.). **B)** Alerta con el texto específico del mockup. |
| N-08 | **Cómo se imprime.** Una aplicación web no controla la impresora directamente; la sección 5.2 indica que la impresión requiere un adaptador externo. | **A)** *Vista previa* muestra el formato de impresión; *Imprimir* abre el diálogo de impresión del navegador, donde se elige la impresora; si el navegador no puede abrirlo, se muestra "Error de impresión, verifique la impresora" y *Reintentar* vuelve a intentarlo. **B)** Impresión directa desde el servidor a una impresora configurada. |
| N-09 | **Cómo se envía el correo.** SD-21 habla de un proveedor de correo, pero el documento no indica cuál. | **A)** Servidor de correo SMTP configurado con variables de entorno (por ejemplo, una cuenta de Gmail con contraseña de aplicación); la factura se envía en el cuerpo del correo. **B)** Postergar R7.5 hasta definir el proveedor. |
| N-10 | ***Actualizar correo* sin edición de clientes.** El curso 2c pide actualizar el correo en la ficha del cliente, pero no existe una pantalla para editar clientes. | **A)** *Actualizar correo* guarda el correo escrito en *Correo del cliente* directamente en el registro del cliente. **B)** Otra forma que indiques. |
| N-11 | **Stock de los productos facturados.** Ningún requisito pide descontar inventario al facturar, aunque el diccionario tiene movimientos de tipo `SALIDA`. | **A)** No se descuenta stock en este módulo. **B)** Se descuenta y se registra un movimiento `SALIDA`. |

## 7. Efecto de la instalación local en N-08 (resuelto: opción B)

Con el sistema instalado en el mismo equipo donde está la impresora, la opción **B** de N-08 (el servidor imprime directamente en la impresora del equipo) se vuelve viable y se ajusta mejor a la interfaz de la Iteración 5:

| Aspecto de la interfaz | Opción A (diálogo del navegador) | Opción B (impresión directa en el equipo) |
|---|---|---|
| Campo *Impresora* | No tiene función: la impresora se elige en el diálogo del navegador. | Lista las impresoras instaladas en el equipo (por ejemplo, "Impresora de recepción"). |
| *Imprimir* | Abre el diálogo del navegador; un paso más para el usuario. | Envía el documento a la impresora elegida. |
| "Error de impresión, verifique la impresora" | Solo si el navegador no puede abrir el diálogo; no detecta una impresora apagada o sin papel. | Se muestra cuando la impresora rechaza o no recibe el trabajo, como describe el curso 2c. |
| *Reintentar* | Vuelve a abrir el diálogo. | Reenvía el documento a la impresora (regreso al paso 2b). |

Recomendación: cambiar N-08 a la opción **B**. Los demás puntos no cambian con la instalación local; el envío de correo (N-09) solo requiere que el equipo tenga internet.

