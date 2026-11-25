# Pruebas manuales — R06 Gestión de reportes

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, base PostgreSQL local, sesión de veterinario.

Resultado de la revisión del responsable: `Aprobado` · `Observado` · `Rechazado`.

Los casos usan las opciones aprobadas en el plan (D-19), que cumplen la indicación del docente: pantallas tal cual el PDF. La verificación previa se hizo con los datos de prueba locales (Tarija, noviembre de 2026).

En todos los casos se revisa además que la pantalla tenga exactamente los campos, columnas, botones y textos del mockup del PDF, sin elementos agregados.

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-R06-01 | CA-10 | Correcto (2026-11-24) | Aprobado (2026-11-24) |
| CP-R06-02 | CA-01 | Correcto (2026-11-24) | Aprobado (2026-11-24) |
| CP-R06-03 | CA-02 | Correcto (2026-11-24) | Aprobado (2026-11-24) |
| CP-R06-04 | CA-03 | Correcto (2026-11-24) | Aprobado (2026-11-24) |
| CP-R06-05 | CA-04 | Correcto (2026-11-24) | Aprobado (2026-11-24) |
| CP-R06-06 | N-06 | Correcto (2026-11-24) | Aprobado (2026-11-24) |
| CP-R06-07 | CA-05 | Correcto (2026-11-24) | Aprobado (2026-11-24) |
| CP-R06-08 | CA-06 | Correcto (2026-11-24) | Aprobado (2026-11-24) |
| CP-R06-09 | CA-07 | Correcto (2026-11-24) | Aprobado (2026-11-24) |
| CP-R06-10 | CA-08 | Correcto (2026-11-24) | Aprobado (2026-11-24) |
| CP-R06-11 | CA-09 | Correcto (2026-11-24) | Aprobado (2026-11-24) |

## Casos

### CP-R06-01 · Acceso a Reportes

- **Requisito / criterio:** R6.1.1 / CA-10
- **Precondición:** sesión iniciada.
- **Pasos:**
  1. En el menú principal, pulsar *Reportes*.
  2. Volver al menú y pulsar *Reportes* en la navegación.
- **Resultado esperado:** ambos accesos abren el módulo de reportes; *Reportes* aparece marcado como activo en la navegación.
- **Verificación previa:** 2026-11-24 · Correcto · *Reportes* abre el reporte de inventario desde el menú principal y desde la navegación, donde aparece activo.
- **Revisión del responsable:** Aprobado (2026-11-24)
- **Observaciones (funcionamiento y visual):**

### CP-R06-02 · Reporte de inventario con productos

- **Requisito / criterio:** R6.1.1–R6.1.3 / CA-01
- **Precondición:** hay productos activos registrados.
- **Pasos:**
  1. En *Tipo de reporte*, elegir *Inventario*.
  2. Pulsar *Generar reporte*.
- **Resultado esperado:** la tabla muestra Producto, Categoría, Stock actual, Stock mínimo y Valor en Bs de cada producto activo. Valor en Bs = stock actual × precio, con formato `1.050,00`.
- **Verificación previa:** 2026-11-24 · Correcto · 21 productos activos con el formato del mockup; por ejemplo, Amoxicilina 250 mg: 15 × Bs 38 = 570,00.
- **Revisión del responsable:** Aprobado (2026-11-24)
- **Observaciones (funcionamiento y visual):**

### CP-R06-03 · Reporte de inventario sin productos

- **Requisito / criterio:** R6.1.4 / CA-02
- **Precondición:** base sin productos activos (por ejemplo, una base de prueba nueva).
- **Pasos:**
  1. Elegir *Inventario* y pulsar *Generar reporte*.
- **Resultado esperado:** se conserva *Inventario* en el tipo de reporte; dentro de la tabla aparece "No hay datos disponibles para este reporte" sin filas ni totales.
- **Verificación previa:** 2026-11-24 · Correcto con base vacía en la prueba automática (lista vacía). En la aplicación hay productos, por lo que el mensaje en pantalla se revisó con el mismo componente en CP-R06-10.
- **Revisión del responsable:** Aprobado (2026-11-24)
- **Observaciones (funcionamiento y visual):**

### CP-R06-04 · Reporte de ingresos de un periodo con facturas

- **Requisito / criterio:** R6.2.1–R6.2.3 / CA-03
- **Precondición:** hay facturas emitidas en el periodo elegido.
- **Pasos:**
  1. Abrir *Reporte de ingresos*.
  2. Elegir *Desde* y *Hasta* que incluyan esas facturas.
  3. Pulsar *Generar reporte*.
- **Resultado esperado:** cada factura del periodo aparece con Fecha, Cliente, Concepto y Monto en Bs, por fecha ascendente; debajo, "Total de ingresos del periodo: Bs N" con la suma de los montos.
- **Verificación previa:** 2026-11-24 · Correcto · noviembre 2026: 8 facturas por fecha ascendente, concepto con las líneas de cada factura y "Total de ingresos del periodo: Bs 2.373,50".
- **Revisión del responsable:** Aprobado (2026-11-24)
- **Observaciones (funcionamiento y visual):**

### CP-R06-05 · Reporte de ingresos sin facturas

- **Requisito / criterio:** R6.2.4 / CA-04
- **Precondición:** un periodo sin facturas (por ejemplo, un mes futuro).
- **Pasos:**
  1. Elegir ese periodo y pulsar *Generar reporte*.
- **Resultado esperado:** las fechas se conservan; aparece "No hay ingresos registrados en el periodo seleccionado" sin filas ni total.
- **Verificación previa:** 2026-11-24 · Correcto · enero 2027 devuelve la lista vacía y total 0; la pantalla usa el mismo estado sin resultados que CP-R06-10.
- **Revisión del responsable:** Aprobado (2026-11-24)
- **Observaciones (funcionamiento y visual):**

### CP-R06-06 · Rango de fechas inválido

- **Requisito / criterio:** precondición de 3.1.14 y 3.1.15 / N-06
- **Precondición:** *Reporte de ingresos* o *Reporte de servicios* abierto.
- **Pasos:**
  1. Abrir la pantalla y mirar las fechas.
  2. Intentar borrar una de las fechas.
  3. Abrir el calendario de *Hasta* e intentar elegir un día anterior a *Desde*.
- **Resultado esperado (N-06, opción C):** al abrir, las fechas traen el primer y el último día del mes actual; no se pueden dejar vacías y los días anteriores a *Desde* aparecen deshabilitados en *Hasta*. No aparece ningún mensaje que no esté en el PDF.
- **Verificación previa:** 2026-11-24 · Correcto · al abrir trae 01/11/2026 y 30/11/2026, sin botón para vaciar; el calendario de *Hasta* muestra deshabilitados los días anteriores a *Desde*. No aparece ningún mensaje nuevo. El backend rechaza un rango invertido con 400.
- **Revisión del responsable:** Aprobado (2026-11-24)
- **Observaciones (funcionamiento y visual):**

### CP-R06-07 · Reporte de servicios de un periodo con citas atendidas

- **Requisito / criterio:** R6.3.1–R6.3.3 / CA-05
- **Precondición:** hay citas `ATENDIDA` en el periodo.
- **Pasos:**
  1. Abrir *Reporte de servicios*.
  2. Elegir *Periodo desde* y *Periodo hasta*.
  3. Pulsar *Generar reporte*.
- **Resultado esperado:** una fila por servicio con Cantidad realizada (citas atendidas) e Ingreso en Bs según N-03, ordenadas de mayor a menor cantidad. Las citas programadas y canceladas no cuentan.
- **Verificación previa:** 2026-11-24 · Correcto · noviembre 2026: 6 servicios con 1 cita atendida cada uno (por ejemplo, Castración de gato 350,00); no cuentan las citas programadas ni canceladas.
- **Revisión del responsable:** Aprobado (2026-11-24)
- **Observaciones (funcionamiento y visual):**

### CP-R06-08 · Reporte de servicios sin citas atendidas

- **Requisito / criterio:** R6.3.4 / CA-06
- **Precondición:** un periodo sin citas atendidas.
- **Pasos:**
  1. Elegir ese periodo y pulsar *Generar reporte*.
- **Resultado esperado:** las fechas se conservan; aparece "No hay servicios registrados en el periodo seleccionado" sin filas.
- **Verificación previa:** 2026-11-24 · Correcto · enero 2027 devuelve la lista vacía (prueba automática y consulta en la aplicación).
- **Revisión del responsable:** Aprobado (2026-11-24)
- **Observaciones (funcionamiento y visual):**

### CP-R06-09 · Reporte de productos por categoría y estado

- **Requisito / criterio:** R6.4.1–R6.4.3 / CA-07
- **Precondición:** hay productos de varias categorías y al menos uno agotado (stock 0).
- **Pasos:**
  1. Abrir *Reporte de productos* con *Todas las categorías* y *Todos*, y pulsar *Generar reporte*.
  2. Elegir una categoría y pulsar *Generar reporte*.
  3. Volver a *Todas las categorías*, elegir *Agotado* y pulsar *Generar reporte*.
- **Resultado esperado:** el paso 1 lista todos los productos activos; los pasos 2 y 3 solo los que cumplen la categoría y el estado elegidos, con Producto, Categoría, Stock y Estado. El selector *Estado* ofrece solo Todos, Disponible y Agotado, como el PDF (N-05, opción B).
- **Verificación previa:** 2026-11-24 · Correcto · *Todas las categorías* y *Todos* listan los productos activos; *Higiene y cuidado* + *Disponible* devuelve 3 productos. El selector *Estado* solo ofrece Todos, Disponible y Agotado.
- **Revisión del responsable:** Aprobado (2026-11-24)
- **Observaciones (funcionamiento y visual):**

### CP-R06-10 · Reporte de productos sin coincidencias

- **Requisito / criterio:** R6.4.4 / CA-08
- **Precondición:** una combinación de categoría y estado que ningún producto cumple.
- **Pasos:**
  1. Elegir esa combinación y pulsar *Generar reporte*.
- **Resultado esperado:** los criterios se conservan; aparece "No hay productos que cumplan el criterio seleccionado" sin filas.
- **Verificación previa:** 2026-11-24 · Correcto · *Todas las categorías* + *Agotado* muestra "No hay productos que cumplan el criterio seleccionado" dentro de la tabla, igual que el estado alternativo del PDF, y conserva los criterios.
- **Revisión del responsable:** Aprobado (2026-11-24)
- **Observaciones (funcionamiento y visual):**

### CP-R06-11 · Los reportes no modifican datos

- **Requisito / criterio:** postcondiciones 3.1.13–3.1.16 / CA-09
- **Precondición:** anotar el stock de un producto, el total de una factura y el estado de una cita.
- **Pasos:**
  1. Generar los cuatro reportes.
  2. Revisar el producto en *Inventario*, la factura en *Facturación* y la cita en *Citas*.
- **Resultado esperado:** los tres valores siguen iguales.
- **Verificación previa:** 2026-11-24 · Correcto · la prueba automática comprueba que el stock, el total de la factura y la cantidad de citas siguen iguales después de generar los reportes; las cuatro operaciones son consultas GET de solo lectura.
- **Revisión del responsable:** Aprobado (2026-11-24)
- **Observaciones (funcionamiento y visual):**
