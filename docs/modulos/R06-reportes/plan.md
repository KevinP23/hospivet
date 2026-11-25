# Plan — R06 Gestión de reportes

| Campo | Valor |
|---|---|
| Estado | Cerrado (pruebas aprobadas por el responsable el 2026-11-24) |
| Versión | 1 |
| Fecha | 2026-11-24 |
| Requisitos | R6.1–R6.4 y sus subfunciones |
| Casos de uso y secuencias | 3.1.13 Reporte de inventario — SD-13 · 3.1.14 Reporte de ingresos — SD-14 · 3.1.15 Reporte de servicios — SD-15 · 3.1.16 Reporte de productos — SD-16 |
| Clases (sección 4.3 y 4.5) | Producto, Factura, Cita, Servicio. "Reporte" se clasifica como **resultado**, no como clase: se obtiene con operaciones de Producto, Factura y Cita. |
| Tablas | `producto`, `factura`, `detalle_factura`, `cliente`, `cita`, `servicio` (solo lectura) |
| Depende de | R04 Inventario, R05 Citas, R07 Facturación, R08 Atención médica (citas `ATENDIDA`), COR-02 (acceso *Reportes* deshabilitado hasta este módulo) |
| Fuente | `Iteracion5_Hospivet.pdf`, págs. 31 (Gestión de reportes), 42–43 (R6), 117–140 (casos 3.1.13 a 3.1.16, interfaces, estados sin resultados y SD-13 a SD-16) y 235–236 (clasificación de "reporte") |

**Nota:** el plan anterior de R06 quedó en la otra computadora sin subirse al repositorio. Este plan se rehízo desde la Iteración 5 en PDF. Si aparece el anterior, se comparan antes de aprobar.

**Regla de este plan:** se implementa exactamente lo que define la Iteración 5 (requisitos, plantillas, interfaces, estados sin resultados y secuencias). No se agregan pantallas, campos, mensajes ni reglas, y no se omite ninguno. Lo que el documento no define se marca en la sección 6 para que el responsable lo resuelva.

**Indicación del docente (comunicada por el responsable el 2026-11-24):** lo que está en el PDF tiene que estar en las pantallas y no se puede agregar nada. Por eso, en la sección 6 solo se proponen opciones que no agregan campos, valores, textos ni mensajes a las cuatro interfaces.

## 1. Requisitos y casos de uso

| Ref. | Función | Categoría | Caso de uso |
|---|---|---|---|
| R6.1.1 | Seleccionar tipo de reporte | Evidente | 3.1.13 |
| R6.1.2 | Obtener datos de productos y stock | Oculto | 3.1.13 |
| R6.1.3 | Mostrar reporte generado | Evidente | 3.1.13 |
| R6.1.4 | Mostrar mensaje "No hay datos disponibles para este reporte" si no hay productos | Evidente | 3.1.13 |
| R6.2.1 | Seleccionar rango de fechas | Evidente | 3.1.14 |
| R6.2.2 | Calcular total de ingresos del periodo | Oculto | 3.1.14 |
| R6.2.3 | Mostrar reporte generado | Evidente | 3.1.14 |
| R6.2.4 | Mostrar mensaje "No hay ingresos registrados en el periodo seleccionado" | Evidente | 3.1.14 |
| R6.3.1 | Seleccionar periodo de servicios | Evidente | 3.1.15 |
| R6.3.2 | Obtener servicios realizados | Oculto | 3.1.15 |
| R6.3.3 | Mostrar reporte generado | Evidente | 3.1.15 |
| R6.3.4 | Mostrar mensaje "No hay servicios registrados en el periodo seleccionado" | Evidente | 3.1.15 |
| R6.4.1 | Seleccionar criterio de productos | Evidente | 3.1.16 |
| R6.4.2 | Obtener listado de productos | Oculto | 3.1.16 |
| R6.4.3 | Mostrar reporte generado | Evidente | 3.1.16 |
| R6.4.4 | Mostrar mensaje "No hay productos que cumplan el criterio seleccionado" | Evidente | 3.1.16 |

Postcondición común: el reporte se muestra **sin modificar registros**. En los cuatro estados sin resultados "se conserva el criterio de búsqueda; no se muestran filas ni totales ficticios".

## 2. Criterios de aceptación

| ID | Origen | Criterio verificable | Mensaje del documento |
|---|---|---|---|
| CA-01 | 3.1.13 pasos 1–2b · SD-13 | Con el tipo *Inventario* seleccionado, *Generar reporte* muestra cada producto con Producto, Categoría, Stock actual, Stock mínimo y Valor en Bs (stock actual × precio; en el mockup 42 × Bs 25 = 1.050,00). | "Reporte de inventario" (SD-13, paso 14) |
| CA-02 | 3.1.13 curso 2c · SD-13 alt | Si no hay productos, se conserva el tipo seleccionado, no se muestran filas y aparece el mensaje dentro de la tabla. | "No hay datos disponibles para este reporte" |
| CA-03 | 3.1.14 pasos 1–2b · SD-14 | Con *Desde* y *Hasta* válidos, *Generar reporte* lista las facturas del periodo (Fecha, Cliente, Concepto, Monto en Bs) y debajo "Total de ingresos del periodo: Bs N", que es la suma de los montos. | "Total de ingresos del periodo: Bs 830,00" (mockup) |
| CA-04 | 3.1.14 curso 2c · SD-14 alt | Sin facturas en el periodo, se conservan las fechas, no se muestran filas ni total y aparece el mensaje. | "No hay ingresos registrados en el periodo seleccionado" |
| CA-05 | 3.1.15 pasos 1–2b · SD-15 | Con *Periodo desde* y *Periodo hasta* válidos, *Generar reporte* agrupa las citas `ATENDIDA` del periodo por servicio: Servicio, Cantidad realizada, Ingreso en Bs. | "Reporte de servicios realizados" (SD-15, paso 14) |
| CA-06 | 3.1.15 curso 2c · SD-15 alt | Sin citas atendidas en el periodo, se conservan las fechas, no se muestran filas y aparece el mensaje. | "No hay servicios registrados en el periodo seleccionado" |
| CA-07 | 3.1.16 pasos 1–2b · SD-16 | Con *Categoría* y *Estado* elegidos, *Generar reporte* lista Producto, Categoría, Stock y Estado de los productos que cumplen ambos criterios. "Todas las categorías" y "Todos" no filtran. | "Reporte de productos" (SD-16) |
| CA-08 | 3.1.16 curso 2c · SD-16 alt | Si ningún producto cumple el criterio, se conservan los criterios, no se muestran filas y aparece el mensaje. | "No hay productos que cumplan el criterio seleccionado" |
| CA-09 | Postcondiciones 3.1.13–3.1.16 | Generar cualquier reporte no crea, cambia ni elimina registros. | — |
| CA-10 | Interfaz del menú principal y navegación (COR-02 N-01) | *Reportes* queda habilitado en la navegación y en el menú principal y abre el módulo. | — |

## 3. Diseño

### 3.1 Datos

No hay tablas ni migraciones nuevas: la sección 4.3 clasifica "reporte" como resultado. Los reportes solo **leen** `producto`, `factura`, `detalle_factura`, `cliente`, `cita` y `servicio`.

### 3.2 Backend — `ReportesController`

| Secuencia | Operación del documento | Endpoint | Fuente de datos |
|---|---|---|---|
| SD-13 | `reporteInventario()` | `GET /api/reportes/inventario` | `producto` |
| SD-14 | `reporteIngresos(desde, hasta)` | `GET /api/reportes/ingresos?desde=AAAA-MM-DD&hasta=AAAA-MM-DD` | `factura` + `cliente` + `detalle_factura` (concepto) |
| SD-15 | `reporteServicios(periodo)` | `GET /api/reportes/servicios?desde=AAAA-MM-DD&hasta=AAAA-MM-DD` | `cita` (`ATENDIDA`) + `servicio` |
| SD-16 | `reporteProductos(criterio)` | `GET /api/reportes/productos?categoria=…&estado=…` | `producto` |

- Paquete `com.hospivet.reporte`: `ReportesController`, `ReporteService` (solo lectura, `@Transactional(readOnly = true)`) y DTO de filas por reporte. Reutiliza los mappers existentes de Producto, Factura, DetalleFactura, Cliente, Cita y Servicio.
- Las categorías del filtro de productos salen del endpoint existente `GET /api/inventario/categorias`.
- Sin resultados, el backend responde `200` con la lista vacía y la interfaz muestra el mensaje exacto, igual que *Consultar citas* (R5.4.7).
- Rango de fechas inválido (falta una fecha o *Hasta* anterior a *Desde*): `400` como protección del backend. La interfaz no permite armar ese rango (N-06), así que no se muestra ningún mensaje adicional.
- Periodo inclusivo: desde las 00:00 de *Desde* hasta el final del día de *Hasta*.
- Importes con 2 decimales y redondeo `HALF_UP`, como en Facturación.

### 3.3 Frontend — interfaces de la Iteración 5

Se habilita *Reportes* en la navegación (`AppHeader.vue`) y en el menú principal (`MenuPrincipalView.vue`), con la ruta `/reportes`. Cada pantalla muestra su título y "Referencias: R6.x, R6.x.1-4", como en los mockups.

| Interfaz | Campos, textos y acciones (tal cual el mockup) | Estado sin resultados |
|---|---|---|
| Reporte de inventario | TIPO DE REPORTE (Inventario); *Generar reporte*; tabla Producto · Categoría · Stock actual · Stock mínimo · Valor en Bs | "No hay datos disponibles para este reporte" dentro de la tabla |
| Reporte de ingresos | DESDE; HASTA; *Generar reporte*; tabla Fecha · Cliente · Concepto · Monto en Bs; "Total de ingresos del periodo: Bs N" | "No hay ingresos registrados en el periodo seleccionado" |
| Reporte de servicios | PERIODO DESDE; PERIODO HASTA; *Generar reporte*; tabla Servicio · Cantidad realizada · Ingreso en Bs | "No hay servicios registrados en el periodo seleccionado" |
| Reporte de productos | CATEGORÍA (Todas las categorías + categorías registradas); ESTADO (Todos, Disponible, Agotado — N-05); *Generar reporte*; tabla Producto · Categoría · Stock · Estado | "No hay productos que cumplan el criterio seleccionado" |

- Fechas con el formato de los mockups (`01/09/2026`) e importes en bolivianos con coma decimal (`1.050,00`, `Bs 830,00`), como en Facturación.
- No se agrega ningún elemento que no esté en los mockups: ni botones de exportar o volver, ni totales en inventario, servicios o productos, ni textos de ayuda.
- Los encabezados de la tabla se ven desde que se abre la pantalla, como en los mockups; las filas (y el total, en ingresos) aparecen al pulsar *Generar reporte*. Cambiar un criterio no borra el resultado anterior hasta volver a generar.
- Orden de las filas: inventario y productos por nombre; ingresos por fecha ascendente (como el mockup: 10/09 y luego 15/09); servicios por cantidad realizada descendente (como el mockup: 24, 15, 9).

## 4. Tareas

- [x] T1 `ReporteService` y `ReportesController` con las cuatro operaciones de SD-13 a SD-16.
- [x] T2 Vistas de las cuatro interfaces y sus estados sin resultados; habilitar *Reportes* en la navegación y el menú.
- [x] T3 Pruebas automatizadas de apoyo del backend (cálculos, filtros, periodos y listas vacías).
- [x] T4 `docs/trazabilidad`: `requisitos-reportes.md`, decisión en `decisiones.md` y puntos para `actualizaciones-pendientes-documentos.md`.
- [x] T5 Verificación previa de `pruebas.md`.

## 5. Lo que no se implementa

Todo lo que no figura en R6.1–R6.4: exportar a PDF o Excel, imprimir o enviar reportes, gráficos, reportes por veterinario y cualquier cambio en los registros. La postcondición de los cuatro casos dice que el reporte se muestra "sin alterar" los datos.

## 6. Puntos que el documento no define

| ID | Qué falta en el documento | Opciones |
|---|---|---|
| N-01 | **Cómo se elige el tipo de reporte.** R6.1.1 pide "Seleccionar tipo de reporte", pero el campo *Tipo de reporte* solo aparece en el mockup de inventario. Las otras tres interfaces no tienen forma de cambiar de reporte. | **A)** *Reportes* abre *Reporte de inventario*. Su campo *Tipo de reporte* ofrece Inventario, Ingresos, Servicios y Productos y cambia a la interfaz elegida. Las otras tres muestran el mismo campo arriba para volver o cambiar; es el único agregado a sus mockups. **B)** Igual que A, pero sin agregar el campo a las otras tres: para cambiar de reporte se vuelve con *Reportes* en la navegación. |
| N-02 | **De quién son los datos.** Facturas y citas guardan el veterinario, y *Consultar citas* muestra solo las del veterinario de la sesión. El documento no dice si los reportes son de toda la veterinaria. | **A)** Toda la veterinaria: la descripción de *Gestión de reportes* habla de "control del negocio". **B)** Solo los registros del veterinario de la sesión. |
| N-03 | **"Ingreso en Bs" del reporte de servicios.** SD-15 consulta Cita y Servicio (no Factura). Los valores del mockup son cantidad × precio (24 × Bs 80 = 1.920,00). | **A)** Cantidad de citas `ATENDIDA` × precio actual del servicio en el catálogo, como el mockup y SD-15. Si el precio cambia, el reporte usa el precio nuevo. **B)** Suma de lo facturado por ese servicio en el periodo (líneas `SERVICIO` de `detalle_factura`). Es el dinero real, pero agrega Factura a SD-15 y no cuenta citas atendidas sin facturar. |
| N-04 | **"Concepto" del reporte de ingresos.** El mockup muestra textos como "Consulta y vacuna", pero la factura no tiene un campo concepto. | **A)** Las descripciones de las líneas de la factura separadas por coma, en el orden del detalle (por ejemplo, "Consulta general, NexGard perros 10 a 25 kg"). **B)** Solo la primera línea y, si hay más, "y N más". |
| N-05 | **Estados del reporte de productos.** El mockup muestra *Disponible* y *Agotado* con el selector en *Todos*. *Inventario* ya usa tres estados: Disponible, Stock bajo (stock ≤ mínimo) y Agotado (stock 0). | **A)** Los mismos tres estados de *Inventario*; el selector ofrece Todos, Disponible, Stock bajo y Agotado. **B)** Solo Disponible (stock > 0) y Agotado (stock 0), como el mockup; el stock bajo cuenta como Disponible. |
| N-06 | **Rango de fechas inválido.** Las precondiciones piden "un rango de fechas válido", pero no hay mensaje para un rango vacío o invertido. | **A)** *Desde* y *Hasta* obligatorios; *Hasta* no puede ser anterior a *Desde*. La regla aparece bajo el campo ("Seleccione la fecha", "La fecha hasta no puede ser anterior a la fecha desde") y no se consulta. Al abrir, los campos traen el primer y el último día del mes actual (el mockup muestra un mes completo). **B)** Igual que A, pero con los campos vacíos al abrir. **C)** Sin mensajes nuevos: al abrir, los campos traen el primer y el último día del mes actual (el mockup muestra un mes completo), no se pueden dejar vacíos y los calendarios no dejan elegir en *Hasta* días anteriores a *Desde* ni en *Desde* días posteriores a *Hasta*. Así no se puede armar un rango inválido. |
| N-07 | **Productos eliminados.** *Eliminar producto* hace una baja lógica (`activo = false`, R04). | **A)** Los reportes de inventario y de productos solo muestran productos activos, igual que *Inventario*. **B)** Incluir los eliminados con un estado "Eliminado". |
| N-08 | **Qué facturas cuentan como ingreso.** Hoy solo existe el estado `EMITIDA` (D-17), pero el diccionario también tiene `ANULADA` y `PAGADA`. | **A)** Todas las facturas salvo las `ANULADA`, para que una futura anulación no cuente como ingreso. **B)** Solo `EMITIDA`. |

**Decisión del responsable (2026-11-24): se aprueba la propuesta siguiente** (N-01 B, N-02 A, N-03 A, N-04 A, N-05 B, N-06 C, N-07 A, N-08 A; decisión D-19). Ninguna opción agrega nada a las pantallas:

| ID | Propuesta | Por qué |
|---|---|---|
| N-01 | **B** | El campo *Tipo de reporte* solo existe en el mockup de inventario; la opción A lo agregaría a las otras tres pantallas. |
| N-02 | **A** | No cambia ninguna pantalla; coincide con "control del negocio". |
| N-03 | **A** | Coincide con los valores del mockup y con SD-15 (Cita y Servicio). |
| N-04 | **A** | No cambia la pantalla; solo define qué texto va en la columna *Concepto* que ya existe. |
| N-05 | **B** | El mockup solo muestra *Disponible* y *Agotado*; la opción A agregaría *Stock bajo*. |
| N-06 | **C** | A y B agregarían mensajes que no están en el PDF. |
| N-07 | **A** | La opción B agregaría el estado "Eliminado". |
| N-08 | **A** | No cambia ninguna pantalla. |

## 7. Riesgos

- **Precios de prueba:** *Consulta general* (Bs 80,00) y el resto del catálogo son de prueba (D-17, N-03 de R07). Con la opción A de N-03, el reporte de servicios mostrará esos precios hasta que se definan los reales.
- **Facturas sin descuento de stock:** el reporte de inventario muestra el stock registrado. Facturar productos no lo descuenta (R07 N-11, opción A).
