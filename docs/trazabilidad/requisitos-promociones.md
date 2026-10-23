# Trazabilidad del módulo de promociones

Fuente: Iteración 5 integrada, casos 3.1.26 a 3.1.29 (R9.1 a R9.4), secuencias SD-26 a SD-29 y diccionario 4.7.15 a 4.7.17. Los documentos Word no se modifican. Plan y pruebas: `docs/modulos/R09-promociones`.

## Requisitos implementados

| Requisito | Caso de uso / secuencia | Implementación |
|---|---|---|
| R9.1, R9.1.1–6 | 3.1.26 Crear promoción · SD-26 `crear(datosPromocion)` | `POST /api/promociones`; mensajes "Complete los campos obligatorios", "Campo obligatorio" y "La fecha de fin debe ser posterior a la de inicio"; respuesta "Promoción creada"; estado inicial `BORRADOR`. |
| R9.2, R9.2.1–5 | 3.1.27 Editar promoción · SD-27 `editar(idPromocion, datos)` | `PUT /api/promociones/{id}`; mensaje "Datos inválidos" con el campo y la regla; respuesta "Promoción actualizada". |
| R9.3, R9.3.1–3 | 3.1.28 Publicar promoción · SD-28 `publicar(idPromocion)` | `PATCH /api/promociones/{id}/publicacion`; mensaje "La promoción ya está publicada"; respuesta "Promoción visible para clientes". |
| R9.4, R9.4.1–4 | 3.1.29 Eliminar promoción · SD-29 `eliminar(idPromocion, confirmar)` | `DELETE /api/promociones/{id}`; al cancelar "Operación cancelada; la promoción se mantiene"; respuesta "Promoción eliminada". |

## Correspondencia con el modelo de datos

Migración `V7__crear_promociones.sql`, con las tablas y columnas del diccionario:

- `promocion`: `titulo`, `descripcion`, `porcentaje_descuento` (0 a 100), `fecha_inicio`, `fecha_fin` (posterior al inicio) y `estado` (`BORRADOR`, `PUBLICADA`, `FINALIZADA`), todos obligatorios.
- `promocion_producto` y `promocion_servicio`: tablas asociativas N:M con clave primaria compuesta.

La factura no referencia a la promoción (diccionario 4.7.13), por lo que eliminar una promoción no afecta facturas.

## Puntos que el documento no definía (resueltos por el responsable el 2026-10-23)

| ID | Decisión |
|---|---|
| N-01 | *Promociones* abre un listado (código, título, vigencia y estado) con acceso a las cuatro operaciones; *Volver al listado* regresa a él. |
| N-02 | *Aplicar a servicio o producto* permite varios servicios y productos y es opcional. |
| N-03 | *Editar promoción* muestra la alerta "Datos inválidos" y, bajo el campo, la regla con los textos de la interfaz. |
| N-04 | El porcentaje de descuento solo admite valores entre 0 y 100. |
| N-05 | El estado `FINALIZADA` no se usa en este módulo. |
| N-06 | *Buscar promoción* es un selector que filtra por código o título. |

## Correspondencia técnica

- Paquete `com.hospivet.promocion`: `PromocionesController`, `PromocionService`, modelos `Promocion`, `PromocionProducto`, `PromocionServicio` y sus mappers.
- Interfaz: ruta `/promociones`, vista `PromocionesView.vue`, opción *Promociones* habilitada en la navegación.
- Código visible `PROM-00001`, derivado del identificador como en el mockup.
