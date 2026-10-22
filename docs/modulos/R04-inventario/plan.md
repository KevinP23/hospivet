# Plan — R04 Gestión de inventario

| Campo | Valor |
|---|---|
| Estado | Implementado (retroactivo) · En revisión |
| Versión | 1 |
| Fecha | 2026-10-21 |
| Requisitos | R4.1–R4.4 y sus subfunciones |
| Casos de uso y secuencias | 3.1.5 Consultar inventario — SD-05 · 3.1.6 Agregar producto — SD-06 · 3.1.7 Eliminar producto — SD-07 · 3.1.8 Actualizar stock — SD-08 |
| Depende de | R01 (veterinario de la sesión para los movimientos) |

## 1. Objetivo

Controlar productos, medicamentos e insumos: consultar existencias, registrar productos, ajustar el stock dejando un movimiento de auditoría y dar de baja productos.

## 2. Alcance

**Incluye:** listado con búsqueda por nombre o código y filtro por categoría, estado calculado (Disponible, Stock bajo, Agotado), alta de producto, actualización de stock con movimiento `AJUSTE`, eliminación lógica con confirmación, siete productos de demostración en bolivianos (V4).

**No incluye:** movimientos de `ENTRADA`/`SALIDA` por compras o ventas (llegarán con facturación), edición de datos del producto, consulta del historial de movimientos.

## 3. Criterios de aceptación

| ID | Requisito | Criterio verificable | Mensaje exacto |
|---|---|---|---|
| CA-01 | R4.1.1–2 | El listado muestra producto, código, categoría, precio en Bs, existencia y estado. | — |
| CA-02 | R4.1.3 | Sin productos o sin coincidencias, la tabla lo indica y conserva los filtros. | "No hay productos registrados" |
| CA-03 | R4.2.1–3 | Sin nombre, categoría, precio, cantidad inicial o stock mínimo no se guarda. | "Complete los campos obligatorios" |
| CA-04 | R4.2.4–5 | Precio o cantidad inicial no positivos, o stock mínimo negativo, no se guarda. | "Precio o cantidad inválidos" |
| CA-05 | R4.2.6 | Con datos válidos se guarda y vuelve al listado. | "Producto guardado en el inventario" |
| CA-06 | R4.3.1–2, R4.3.4 | Eliminar muestra el producto y su existencia y exige confirmar; al confirmar desaparece del listado. | "Producto eliminado del inventario" |
| CA-07 | R4.3.3 | Cancelar la eliminación vuelve al listado sin cambios. | — |
| CA-08 | R4.4.1–2 | Buscar un producto inexistente informa el error. | "Producto no encontrado" |
| CA-09 | R4.4.3–6 | Se ve la existencia actual; una cantidad vacía, negativa o con decimales no se guarda. | "Cantidad inválida" |
| CA-10 | R4.4.7 | Una cantidad válida y distinta actualiza el stock y registra un movimiento con el veterinario de la sesión. | "Existencia actualizada correctamente" |

## 4. Diseño implementado

### 4.1 Datos
`producto` y `movimiento_inventario` (V2), productos iniciales (V4). Diferencia con el diccionario 4.7.12: faltan `stock_anterior` y `stock_resultante` (AUD-05).

### 4.2 Backend
| Método | Ruta | Descripción | Errores |
|---|---|---|---|
| GET | `/api/inventario/productos?buscar=&categoria=` | Productos activos filtrados | 401/403 |
| POST | `/api/inventario/productos` | Agrega un producto | 400 por campo |
| PATCH | `/api/inventario/productos/{id}/stock` | Actualiza stock y registra movimiento | 400, 404 |
| DELETE | `/api/inventario/productos/{id}` | Baja lógica (`activo = false`) | 404 |

Clases: `InventarioController`, `InventarioService`, `ProductoCreateRequest`, `StockUpdateRequest`, `ProductoResponse`, `Producto`, `MovimientoInventario` y sus mappers.

### 4.3 Frontend
`InventarioView.vue` (ruta `/inventario`) con cuatro estados: listado, agregar, actualizar stock y confirmación de eliminación.

## 5. Decisiones y diferencias con la documentación

| ID | Decisión | Motivo |
|---|---|---|
| — | Código visible derivado `PRD-000001`. | La interfaz busca por código, pero el diccionario no define la columna. |
| — | Eliminación lógica. | Conservar movimientos y trazabilidad. |
| — | Todo cambio manual de stock es `AJUSTE`. | R4.4 es un ajuste manual; entradas y salidas llegan con facturación. |

## 6. Correcciones (aprobadas el 2026-10-21 · codificadas en COR-01 · pruebas de COR-01 aprobadas el 2026-10-22)

- [x] AUD-01 (alta): rechazar *Nueva cantidad* vacía; hoy se guarda como 0.
- [x] AUD-05: migración V5 con `stock_anterior` y `stock_resultante` en `movimiento_inventario`, completando los movimientos existentes desde su motivo.
- [x] AUD-07: categorías desde todo el inventario; búsqueda de stock parcial y sobre todo el inventario.
- [x] AUD-10: limitar el precio a 8 enteros y 2 decimales.
- [x] AUD-14: unificar la obtención del veterinario de la sesión.
- [ ] AUD-O4: decidir el color del botón "Sí, eliminar producto" (acción destructiva). → mejora opcional OP-01, no incluida.

## 7. Riesgos y preguntas abiertas

- El filtro de categoría usa coincidencia parcial ("Alimento" también incluye "Alimento húmedo").
