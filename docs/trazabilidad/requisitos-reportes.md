# Trazabilidad del módulo de reportes

Fuente: Iteración 5 (`Iteracion5_Hospivet.pdf`), casos 3.1.13 a 3.1.16 (R6.1 a R6.4), sus interfaces y estados sin resultados, secuencias SD-13 a SD-16 y la clasificación de "reporte" en la sección 4.3. Los documentos originales no se modifican. Plan y pruebas: `docs/modulos/R06-reportes`.

Indicación del docente (2026-11-24): las pantallas deben contener exactamente lo que muestra el PDF, sin agregados.

## Requisitos implementados

| Requisito | Caso de uso / secuencia | Implementación |
|---|---|---|
| R6.1, R6.1.1–4 | 3.1.13 Reporte de inventario · SD-13 `reporteInventario()` | `GET /api/reportes/inventario`; tabla Producto, Categoría, Stock actual, Stock mínimo y Valor en Bs (stock actual × precio); mensaje "No hay datos disponibles para este reporte". |
| R6.2, R6.2.1–4 | 3.1.14 Reporte de ingresos · SD-14 `reporteIngresos(desde, hasta)` | `GET /api/reportes/ingresos`; tabla Fecha, Cliente, Concepto y Monto en Bs; "Total de ingresos del periodo: Bs N"; mensaje "No hay ingresos registrados en el periodo seleccionado". |
| R6.3, R6.3.1–4 | 3.1.15 Reporte de servicios · SD-15 `reporteServicios(periodo)` | `GET /api/reportes/servicios`; citas `ATENDIDA` del periodo por servicio: Servicio, Cantidad realizada e Ingreso en Bs; mensaje "No hay servicios registrados en el periodo seleccionado". |
| R6.4, R6.4.1–4 | 3.1.16 Reporte de productos · SD-16 `reporteProductos(criterio)` | `GET /api/reportes/productos`; criterios Categoría y Estado; tabla Producto, Categoría, Stock y Estado; mensaje "No hay productos que cumplan el criterio seleccionado". |

## Correspondencia con el modelo de datos

No hay tablas ni migraciones nuevas: la sección 4.3 clasifica "reporte" como resultado que se obtiene con operaciones de Producto, Factura y Cita. Los reportes solo leen `producto`, `factura`, `detalle_factura`, `cliente`, `cita` y `servicio`, y no modifican ningún registro (postcondición de los cuatro casos).

## Puntos que el documento no definía (resueltos por el responsable el 2026-11-24, D-19)

| ID | Decisión |
|---|---|
| N-01 | Opción B: *Reportes* abre *Reporte de inventario*; su campo *Tipo de reporte* (Inventario, Ingresos, Servicios, Productos) cambia a la interfaz elegida. Las otras tres pantallas no agregan ese campo; se vuelve con *Reportes* en la navegación. |
| N-02 | Los reportes muestran los datos de toda la veterinaria. |
| N-03 | Ingreso en Bs del reporte de servicios = cantidad de citas `ATENDIDA` × precio actual del servicio en el catálogo. |
| N-04 | *Concepto* son las descripciones de las líneas de la factura, separadas por coma. |
| N-05 | Opción B: estados *Disponible* (stock mayor que 0) y *Agotado* (stock 0), como el mockup. |
| N-06 | Opción C: las fechas empiezan con el mes actual, no se pueden vaciar y los calendarios no permiten un rango invertido; no se agregan mensajes. El backend rechaza un rango inválido con `400` como protección. |
| N-07 | Solo se muestran productos activos. |
| N-08 | Cuentan como ingreso todas las facturas salvo las `ANULADA`. |

## Correspondencia técnica

- Paquete `com.hospivet.reporte`: `ReportesController`, `ReporteService` (solo lectura) y los DTO de cada reporte. Reutiliza los mappers de Producto, Factura, DetalleFactura, Cliente, Cita y Servicio.
- Seguridad: `/api/reportes/**` exige el rol `VETERINARIO`, igual que los demás módulos.
- Interfaz: vista `ReportesView.vue` con las rutas `/reportes` (inventario), `/reportes/ingresos`, `/reportes/servicios` y `/reportes/productos`. *Reportes* queda habilitado en la navegación y en el menú principal.
- Formatos de los mockups: fechas `01/09/2026`, importes `1.050,00` en las celdas y `Bs 830,00` en el total.
