# Trazabilidad del módulo de facturación

Fuente: Iteración 5 integrada, casos 3.1.17 a 3.1.21 (R7.1 a R7.5), secuencias SD-17 a SD-21 y diccionario 4.7.13 y 4.7.14. Los documentos Word no se modifican. Plan y pruebas: `docs/modulos/R07-facturacion`.

## Requisitos implementados

| Requisito | Caso de uso / secuencia | Implementación |
|---|---|---|
| R7.1, R7.1.1–3 | 3.1.17 Revisar factura · SD-17 `consultarDetalle(idFactura)` | `GET /api/facturas/{id}`; mensaje "Factura no encontrada"; muestra cliente, fecha de emisión, estado, detalle y totales. |
| R7.2, R7.2.1–5 | 3.1.18 Editar factura · SD-18 `editar(idFactura, datos, items)` | `PUT /api/facturas/{id}`; mensaje "Datos inválidos" con el campo y la regla; recalcula subtotales y total; respuesta "Factura actualizada". |
| R7.3, R7.3.1–6 | 3.1.19 Generar factura · SD-19 `generar(cliente, items)` | `POST /api/facturas`; mensaje "Servicio o producto no encontrado"; suma subtotales, aplica la promoción vigente y guarda cabecera y detalle en una transacción; respuesta "Factura generada con total calculado". |
| R7.4, R7.4.1–3 | 3.1.20 Imprimir factura · SD-20 `imprimir(idFactura)` | `GET /api/impresoras` y `POST /api/facturas/{id}/impresion`; mensaje "Error de impresión, verifique la impresora"; respuesta "Documento preparado para impresión". |
| R7.5, R7.5.1–3 | 3.1.21 Enviar factura · SD-21 `enviar(idFactura, medio)` | `POST /api/facturas/{id}/envio`; mensaje "El cliente no tiene correo registrado"; respuesta "Solicitud de envío confirmada por proveedor". |

## Correspondencia con el modelo de datos

Migración `V8__crear_facturacion.sql`, con las tablas y columnas del diccionario:

- `factura`: `id_cliente`, `id_veterinario` (de la sesión), `fecha_hora`, `subtotal`, `descuento` (de 0 al subtotal), `total` (= subtotal − descuento) y `estado` (`EMITIDA`, `ANULADA`, `PAGADA`), todos obligatorios.
- `detalle_factura`: `id_factura`, `id_producto` o `id_servicio` (exactamente uno, según `tipo_item`), `descripcion` (texto congelado del ítem), `cantidad` > 0, `precio_unitario` ≥ 0 y `subtotal`.

La factura no referencia a la promoción (diccionario 4.7.13): el descuento aplicado queda guardado como importe.

## Puntos que el documento no definía (resueltos por el responsable el 2026-10-27)

| ID | Decisión |
|---|---|
| N-01 | *Facturación* abre un listado (número, cliente, fecha, total y estado) con acceso a las cinco operaciones. |
| N-02 | Agregar el mismo ítem suma 1 a su cantidad; el precio unitario es el del catálogo. Cantidad y precio se corrigen con *Editar factura*. |
| N-03 | *Consulta general* pasa a Bs 80,00 (valor del mockup), precio de prueba que el responsable cambiará por el real. |
| N-04 | *Promoción vigente* ofrece las promociones *Publicadas* vigentes en la fecha de emisión y asociadas a algún ítem del detalle; el porcentaje se aplica a los ítems asociados (a todo el subtotal si la promoción no tiene ítems asociados). |
| N-05 | Solo se usa el estado `EMITIDA`. |
| N-06 | Un selector elige la línea del detalle y los campos del mockup editan esa línea. |
| N-07 | *Editar factura* muestra la alerta "Datos inválidos" y la regla bajo el campo. |
| N-08 | Opción B: el sistema imprime directamente en la impresora del equipo elegida en *Impresora* (instalación local, D-16). |
| N-09 | El correo se envía por SMTP configurado con variables de entorno (`HOSPIVET_MAIL_HOST`, `HOSPIVET_MAIL_PORT`, `HOSPIVET_MAIL_USERNAME`, `HOSPIVET_MAIL_PASSWORD`, `HOSPIVET_MAIL_FROM`); la factura va en el cuerpo del correo. |
| N-10 | *Actualizar correo* guarda el correo escrito directamente en el registro del cliente. |
| N-11 | Facturar no descuenta stock. |

## Correspondencia técnica

- Paquete `com.hospivet.factura`: `FacturacionController`, `FacturaService`, `FacturaDocumentoService` (impresión y correo), `FacturaImprimible` (formato de impresión), modelos `Factura` y `DetalleFactura` y sus mappers.
- Interfaz: ruta `/facturacion`, vista `FacturacionView.vue`, opción *Facturación* habilitada en la navegación.
- Número visible de 6 dígitos (`000124`), derivado del identificador como en los mockups; importes con el formato `Bs 80,00`.
