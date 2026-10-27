# Pruebas manuales — R07 Gestión de facturación

Entorno: backend en `http://localhost:8080`, frontend en `http://localhost:5173/`, base PostgreSQL local con la migración V8 aplicada, sesión de veterinario, un cliente con correo y otro sin correo, servicios y productos con precio, y una promoción *Publicada* vigente asociada a un servicio, módulo *Facturación*.

La verificación previa se hizo con una base H2 temporal en memoria y una cuenta veterinaria de prueba creada solo para esa sesión; no se usó la base PostgreSQL del responsable.

Cada caso corresponde a un criterio del plan, que sale de la plantilla, la interfaz y el diagrama de secuencia del caso de uso. Los casos CP-R07-12 a CP-R07-17 verifican las decisiones N-01 a N-11 (D-17). N-05 se verifica en CP-R07-03, N-07 en CP-R07-06, N-08 en CP-R07-08 y CP-R07-09, y N-09 en CP-R07-11.

## Resumen

| Caso | Criterio | Caso de uso / secuencia | Verificación previa | Revisión del responsable |
|---|---|---|---|---|
| CP-R07-01 | CA-03, CA-05 | 3.1.19 / SD-19 | Correcto | Aprobado |
| CP-R07-02 | CA-04 | 3.1.19 curso 2c / SD-19 | Correcto | Aprobado |
| CP-R07-03 | CA-01 | 3.1.17 / SD-17 | Correcto | Aprobado |
| CP-R07-04 | CA-02 | 3.1.17 curso 2c / SD-17 | Correcto | Aprobado |
| CP-R07-05 | CA-06 | 3.1.18 pasos 1–2 / SD-18 | Correcto | Aprobado |
| CP-R07-06 | CA-07 | 3.1.18 curso 4b / SD-18 | Correcto | Aprobado |
| CP-R07-07 | CA-08 | 3.1.18 pasos 4a–5 / SD-18 | Correcto | Aprobado |
| CP-R07-08 | CA-09 | 3.1.20 / SD-20 | Correcto | Aprobado |
| CP-R07-09 | CA-10 | 3.1.20 curso 2c / SD-20 | Correcto | Aprobado |
| CP-R07-10 | CA-11 | 3.1.21 curso 2c / SD-21 | Correcto | Aprobado |
| CP-R07-11 | CA-12 | 3.1.21 / SD-21 | Correcto | Aprobado |
| CP-R07-12 | N-01 | Listado de facturas | Correcto | Aprobado |
| CP-R07-13 | N-02, N-03 | 3.1.19 / SD-19 | Correcto | Aprobado |
| CP-R07-14 | N-04 | 3.1.19 / SD-19 | Correcto | Aprobado |
| CP-R07-15 | N-06 | 3.1.18 / SD-18 | Correcto | Aprobado |
| CP-R07-16 | N-10 | 3.1.21 curso 2c / SD-21 | Correcto | Aprobado |
| CP-R07-17 | N-11 | 3.1.19 / SD-19 | Correcto | Aprobado |

## Casos

### CP-R07-01 · Generar factura
- **Requisito / criterio:** R7.3.1–2, R7.3.4–6 / CA-03, CA-05
- **Pasos:** 1. *Generar factura*: elegir el cliente con correo. 2. Agregar al detalle un servicio y un producto. 3. Revisar *Promoción vigente*. 4. *Confirmar y generar factura*.
- **Resultado esperado:** la tabla muestra cada ítem con cantidad, precio unitario y subtotal; Subtotal, Descuento (según la promoción) y Total = Subtotal − Descuento; "Factura generada con total calculado".
- **Verificación previa:** 2026-10-27 · Correcto · cliente Ana Pérez, *Consulta general* ×2 y *Antiparasitario* ×1: subtotal Bs 195,50, descuento Bs 16,00, total Bs 179,50; mensaje "Factura generada con total calculado" y la factura 000001 aparece en el listado como *Emitida*.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-02 · Servicio o producto no encontrado
- **Requisito / criterio:** R7.3.3 / CA-04
- **Pasos:** 1. Agregar un producto al detalle. 2. Antes de confirmar, eliminar ese producto desde *Inventario* (otra pestaña). 3. *Confirmar y generar factura*.
- **Resultado esperado:** "Servicio o producto no encontrado"; no se genera la factura y no se muestran totales ficticios.
- **Verificación previa:** 2026-10-27 · Correcto · con *Collar isabelino* en el detalle se dio de baja el producto desde otra sesión; al confirmar aparece "Servicio o producto no encontrado", la tabla queda sin filas ni totales y no se genera la factura.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-03 · Revisar factura
- **Requisito / criterio:** R7.1.1, R7.1.3 / CA-01
- **Pasos:** 1. *Revisar factura*: buscar el número de la factura de CP-R07-01. 2. *Buscar factura*.
- **Resultado esperado:** cliente, fecha de emisión, estado *Emitida*, detalle y totales iguales a los de la generación.
- **Verificación previa:** 2026-10-27 · Correcto · 000001 muestra cliente Ana Pérez, fecha 27/10/2026, estado *Emitida*, el detalle y los totales de la generación.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-04 · Factura no encontrada
- **Requisito / criterio:** R7.1.2 / CA-02
- **Pasos:** 1. Buscar un número de factura que no existe.
- **Resultado esperado:** "Factura no encontrada"; el número buscado se conserva; no aparecen filas ni totales.
- **Verificación previa:** 2026-10-27 · Correcto · 000099 → "Factura no encontrada"; el número se conserva y no hay filas ni totales.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-05 · Buscar factura a editar
- **Requisito / criterio:** R7.2.1 / CA-06
- **Pasos:** 1. *Editar factura*: buscar la factura de CP-R07-01.
- **Resultado esperado:** se muestran cliente, fecha, detalle (descripción, cantidad, precio) y descuento actuales.
- **Verificación previa:** 2026-10-27 · Correcto · se muestran cliente, fecha, línea *Consulta general* (cantidad 2, precio 80.00) y descuento 16.00.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-06 · Datos inválidos
- **Requisito / criterio:** R7.2.3–4 / CA-07
- **Pasos:** 1. Cantidad `-1` → *Guardar cambios*. 2. Restaurar; precio `-5` → guardar. 3. Restaurar; descripción vacía → guardar. 4. Restaurar; descuento mayor que el subtotal → guardar.
- **Resultado esperado:** en cada intento, "Datos inválidos" con el campo identificado y la regla explicada; la factura conserva sus datos anteriores.
- **Verificación previa:** 2026-10-27 · Correcto · cantidad -1, precio -5, descripción vacía y descuento 999 → alerta "Datos inválidos" con "Cantidad mayor que 0", "Precio no negativo", "Descripción obligatoria" y "Descuento entre 0 y el subtotal" bajo el campo; la factura no cambia.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-07 · Guardar cambios
- **Requisito / criterio:** R7.2.5 / CA-08
- **Pasos:** 1. Cantidad `2` en una línea. 2. *Guardar cambios*. 3. Revisar la factura en *Revisar factura*.
- **Resultado esperado:** "Factura actualizada"; subtotal de la línea, subtotal y total recalculados.
- **Verificación previa:** 2026-10-27 · Correcto · "Factura actualizada"; *Consulta general* 1 × Bs 70,00 y *Antiparasitario* 3 × Bs 35,50 → subtotal Bs 176,50, descuento Bs 16,00, total Bs 160,50.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-08 · Imprimir factura
- **Requisito / criterio:** R7.4.1–2 / CA-09
- **Pasos:** 1. *Imprimir factura*: seleccionar la factura. 2. *Vista previa*. 3. *Imprimir*.
- **Resultado esperado:** la vista previa muestra el detalle y el *Total a pagar*; se envía a imprimir; "Documento preparado para impresión".
- **Verificación previa:** 2026-10-27 · Correcto · vista previa con el detalle y "Total a pagar: Bs 160,50"; *Imprimir* en la impresora *Adobe PDF* del equipo generó el documento "Factura 000001" con el formato completo y respondió "Documento preparado para impresión".
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-09 · Error de impresión
- **Requisito / criterio:** R7.4.3 / CA-10
- **Pasos:** 1. *Imprimir factura*: seleccionar la factura y una impresora apagada, desconectada o sin papel. 2. *Imprimir*. 3. Corregir la impresora y pulsar *Reintentar*.
- **Resultado esperado:** "Error de impresión, verifique la impresora"; se conserva la vista previa; *Reintentar* vuelve a enviar.
- **Verificación previa:** 2026-10-27 · Correcto · con una impresora no disponible → "Error de impresión, verifique la impresora"; la vista previa se conserva y *Reintentar* queda habilitado. Se simuló la impresora no disponible; con una impresora física apagada lo revisa el responsable.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-10 · Cliente sin correo
- **Requisito / criterio:** R7.5.2 / CA-11
- **Pasos:** 1. Generar una factura para el cliente sin correo. 2. *Enviar factura* con medio *Correo electrónico*.
- **Resultado esperado:** "El cliente no tiene correo registrado" en la alerta y bajo *Correo del cliente*; no se envía.
- **Verificación previa:** 2026-10-27 · Correcto · factura 000002 de Luis Rojas → "El cliente no tiene correo registrado" en la alerta y bajo *Correo del cliente*.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-11 · Enviar factura
- **Requisito / criterio:** R7.5.1, R7.5.3 / CA-12
- **Pasos:** 1. *Enviar factura* de la factura de CP-R07-01 (cliente con correo). 2. *Enviar factura*. 3. Revisar la bandeja de entrada de ese correo.
- **Resultado esperado:** "Solicitud de envío confirmada por proveedor"; el correo llega con la factura. Requiere configurar en el equipo el servidor de correo (N-09); si el proveedor no confirma el envío se muestra "No fue posible enviar la factura", mensaje que el documento no define.
- **Verificación previa:** 2026-10-27 · Correcto · sin servidor de correo el sistema mostraba "No fue posible enviar la factura"; con el correo de Gmail configurado en el equipo (N-09), el responsable envió la factura 000001 y obtuvo "Solicitud de envío confirmada por proveedor".
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-12 · Listado de facturas
- **Requisito / criterio:** N-01 (D-17)
- **Pasos:** 1. Abrir *Facturación* en la navegación. 2. Revisar el listado. 3. Usar *Revisar*, *Editar*, *Imprimir* y *Enviar* de una fila y *Volver al listado*.
- **Resultado esperado:** el listado muestra número de 6 dígitos, cliente, fecha, total y estado de cada factura; cada acción abre su interfaz con la factura elegida y *Volver al listado* regresa.
- **Verificación previa:** 2026-10-27 · Correcto · el listado muestra 000001 y 000002 con cliente, fecha, total y estado; *Revisar*, *Editar*, *Imprimir* y *Enviar* abren la factura elegida y *Volver al listado* regresa.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-13 · Cantidad y precio del catálogo
- **Requisito / criterio:** N-02, N-03 (D-17)
- **Pasos:** 1. *Generar factura*: elegir un cliente. 2. Agregar *Consulta general* dos veces con *Agregar al detalle*.
- **Resultado esperado:** una sola fila de *Consulta general* con cantidad 2, precio unitario Bs 80,00 y subtotal Bs 160,00.
- **Verificación previa:** 2026-10-27 · Correcto · *Consulta general* agregada dos veces → una fila con cantidad 2, Bs 80,00 y subtotal Bs 160,00.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-14 · Promoción vigente
- **Requisito / criterio:** N-04 (D-17)
- **Pasos:** 1. *Generar factura*: agregar el servicio asociado a la promoción vigente y un producto no asociado. 2. Abrir *Promoción vigente* y elegirla. 3. Confirmar.
- **Resultado esperado:** solo se ofrecen promociones *Publicadas*, vigentes hoy y asociadas a algún ítem del detalle; el descuento es el porcentaje sobre el subtotal del servicio asociado, no sobre el producto.
- **Verificación previa:** 2026-10-27 · Correcto · "Consulta con 10 %" (asociada a *Consulta general*) se ofrece con la consulta en el detalle y no con solo el collar; el descuento fue Bs 16,00 = 10 % de Bs 160,00, sin incluir el producto.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-15 · Edición por línea
- **Requisito / criterio:** N-06 (D-17)
- **Pasos:** 1. *Editar factura* de una factura con dos líneas. 2. Elegir la segunda en *Línea del detalle* y cambiar su cantidad. 3. Volver a la primera y cambiar su precio. 4. *Guardar cambios*.
- **Resultado esperado:** los campos muestran los datos de la línea elegida; al guardar se conservan los cambios de ambas líneas y se recalculan los totales.
- **Verificación previa:** 2026-10-27 · Correcto · cantidad 3 en *Antiparasitario* y luego cantidad 1 en *Consulta general*; al guardar se conservan ambos cambios.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-R07-16 · Actualizar correo
- **Requisito / criterio:** N-10 (D-17)
- **Pasos:** 1. *Enviar factura* del cliente sin correo. 2. Escribir un correo inválido y pulsar *Actualizar correo*. 3. Escribir un correo válido y pulsar *Actualizar correo*.
- **Resultado esperado:** con el correo inválido aparece "Correo inválido" bajo *Correo del cliente*; con el válido el correo queda guardado en el registro del cliente y se puede enviar la factura.
- **Verificación previa:** 2026-10-27 · Correcto · "luis@" → "Correo inválido" bajo *Correo del cliente*; "luis@example.test" queda guardado en el registro del cliente.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):** *Actualizar correo* no muestra un mensaje de confirmación porque el documento no define uno.

### CP-R07-17 · Stock sin cambios
- **Requisito / criterio:** N-11 (D-17)
- **Pasos:** 1. Anotar el stock de un producto en *Inventario*. 2. Generar una factura con ese producto. 3. Revisar de nuevo el stock.
- **Resultado esperado:** el stock no cambia y no se registra un movimiento de salida.
- **Verificación previa:** 2026-10-27 · Correcto · el stock de *Antiparasitario* sigue en 20 después de facturar 3 unidades.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**
