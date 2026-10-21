# Trazabilidad del módulo de inventario

Fuente funcional: iteraciones 4 y 5 de HOSPIVET. Los documentos Word se mantienen sin modificaciones.

| Requisito | Comportamiento implementado | Validaciones y estados | Implementación |
|---|---|---|---|
| R4.1, R4.1.1–3 Consultar inventario | Lista productos activos, permite buscar por nombre o código derivado y filtrar por categoría. | Cuando no existen coincidencias, la tabla muestra `No hay productos registrados` y conserva los filtros. | `GET /api/inventario/productos`, `InventarioView.vue` |
| R4.2, R4.2.1–6 Agregar producto | Registra nombre, categoría, descripción, precio, cantidad inicial y stock mínimo. | `Complete los campos obligatorios`; `Precio o cantidad inválidos`. El formulario solo se limpia después de guardar correctamente. | `POST /api/inventario/productos` |
| R4.3, R4.3.1–4 Eliminar producto | Muestra producto y existencia; exige pulsar `Sí, eliminar producto`. | Cancelar vuelve al inventario sin cambios. La eliminación es lógica (`activo=false`) para conservar la trazabilidad. | `DELETE /api/inventario/productos/{id}` |
| R4.4, R4.4.1–7 Actualizar stock | Busca por nombre o código, muestra existencia actual y guarda una cantidad no negativa. | `Producto no encontrado`; `Cantidad inválida`. Cada cambio real genera un movimiento `AJUSTE`. | `PATCH /api/inventario/productos/{id}/stock` |

## Correspondencia con el modelo de datos

- `Producto` se materializa en la tabla `producto`.
- `MovimientoInventario` se materializa en `movimiento_inventario` y referencia a `Producto` y `Veterinario`.
- El estado visible se calcula: `Agotado` si el stock es cero, `Stock bajo` si es menor o igual al mínimo y `Disponible` en los demás casos.
- La interfaz solicita búsqueda por código, pero el diccionario de Iteración 4 no define un atributo persistente `codigo`. Para no agregar un dato no documentado, el sistema muestra un código derivado del identificador con formato `PRD-000001`.

## Migración

Flyway V2 crea las tablas e índices del módulo sin borrar ni reemplazar registros existentes.
## Datos iniciales para Bolivia

La migración `V4__agregar_productos_iniciales_bolivia.sql` añade siete productos de ejemplo disponibles en categorías habituales para perros y gatos. Los precios están expresados en bolivianos y se tomaron como referencias de comercios bolivianos consultados el 21/10/2026. Son datos editables de demostración y no una lista oficial ni permanente de precios.

La migración conserva los productos existentes y evita duplicar cualquiera de los nombres iniciales.

