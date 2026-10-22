# Pruebas manuales — R04 Gestión de inventario

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, sesión de veterinario iniciada, productos de demostración de la migración V4 cargados, módulo *Inventario*.

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-R04-01 | CA-01 | Pendiente | Pendiente |
| CP-R04-02 | CA-01, CA-02 | Pendiente | Pendiente |
| CP-R04-03 | CA-03 | Pendiente | Pendiente |
| CP-R04-04 | CA-04 | Pendiente | Pendiente |
| CP-R04-05 | CA-05 | Pendiente | Pendiente |
| CP-R04-06 | CA-08 | Pendiente | Pendiente |
| CP-R04-07 | CA-09 | Pendiente | Pendiente |
| CP-R04-08 | CA-10 | Pendiente | Pendiente |
| CP-R04-09 | CA-07 | Pendiente | Pendiente |
| CP-R04-10 | CA-06 | Pendiente | Pendiente |

## Casos

### CP-R04-01 · Listado inicial
- **Requisito / criterio:** R4.1.1–2 / CA-01
- **Pasos:** 1. Abrir *Inventario*.
- **Resultado esperado:** siete productos de demostración con código `PRD-…`, categoría, precio en Bs, existencia y estado "Disponible".
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R04-02 · Búsqueda y filtros
- **Requisito / criterio:** R4.1 / CA-01, CA-02
- **Pasos:** 1. Buscar `pedigree` y presionar *Buscar*. 2. Limpiar, elegir la categoría *Higiene para gatos* y buscar. 3. Abrir de nuevo el selector de categoría. 4. Buscar `zzz`.
- **Resultado esperado:** 1 → solo Pedigree. 2 → solo Arena sanitaria. 3 → siguen todas las categorías. 4 → "No hay productos registrados" y el texto `zzz` se conserva.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R04-03 · Agregar sin campos obligatorios
- **Requisito / criterio:** R4.2.2–3 / CA-03
- **Pasos:** 1. *Agregar producto*. 2. Escribir solo el nombre y presionar *Guardar producto*.
- **Resultado esperado:** "Complete los campos obligatorios"; el nombre se conserva.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R04-04 · Precio o cantidad inválidos
- **Requisito / criterio:** R4.2.4–5 / CA-04
- **Pasos:** 1. Completar todo con precio `0`. 2. Guardar. 3. Corregir precio a `25` y poner cantidad inicial `-1`. 4. Guardar.
- **Resultado esperado:** ambos intentos muestran "Precio o cantidad inválidos" y no guardan.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R04-05 · Agregar producto correcto
- **Requisito / criterio:** R4.2.6 / CA-05
- **Pasos:** 1. Nombre `Vacuna antirrábica`, categoría `Medicamentos`, precio `35`, cantidad inicial `10`, stock mínimo `3`. 2. Guardar.
- **Resultado esperado:** "Producto guardado en el inventario"; aparece en el listado con estado "Disponible".
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R04-06 · Producto no encontrado
- **Requisito / criterio:** R4.4.1–2 / CA-08
- **Pasos:** 1. Presionar *Stock* en cualquier fila. 2. Borrar el buscador, escribir `inexistente` y presionar *Buscar producto*.
- **Resultado esperado:** "Producto no encontrado".
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R04-07 · Cantidad inválida
- **Requisito / criterio:** R4.4.5–6 / CA-09
- **Pasos:** 1. *Stock* en *Vacuna antirrábica*. 2. Nueva cantidad `-5` → *Guardar cambios*. 3. Nueva cantidad `2.5` → guardar. 4. Borrar el campo y guardar.
- **Resultado esperado:** los tres intentos muestran "Cantidad inválida" y el stock no cambia.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R04-08 · Actualizar stock correcto
- **Requisito / criterio:** R4.4.7 / CA-10
- **Pasos:** 1. *Stock* en *Vacuna antirrábica*, nueva cantidad `3`. 2. Guardar.
- **Resultado esperado:** "Existencia actualizada correctamente"; existencia 3 y estado "Stock bajo". Con `0`, estado "Agotado".
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R04-09 · Cancelar eliminación
- **Requisito / criterio:** R4.3.3 / CA-07
- **Pasos:** 1. *Eliminar* en *Vacuna antirrábica*. 2. Presionar *Cancelar*.
- **Resultado esperado:** vuelve al listado y el producto sigue ahí.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**

### CP-R04-10 · Eliminar producto
- **Requisito / criterio:** R4.3.1–2, R4.3.4 / CA-06
- **Pasos:** 1. *Eliminar* en *Vacuna antirrábica*. 2. Revisar nombre y existencia. 3. *Sí, eliminar producto*.
- **Resultado esperado:** "Producto eliminado del inventario"; ya no aparece en el listado.
- **Verificación previa:** Pendiente
- **Revisión del responsable:** Pendiente
- **Observaciones (funcionamiento y visual):**
