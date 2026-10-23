# Plan — R08 Gestión de atención médica

| Campo | Valor |
|---|---|
| Estado | Cerrado (pruebas aprobadas por el responsable el 2026-10-23) |
| Versión | 2 (ajustada a la trazabilidad exacta de la Iteración 5) |
| Fecha | 2026-10-22 |
| Requisitos | R8.1–R8.4 y sus subfunciones |
| Casos de uso y secuencias | 3.1.22 Consultar historial — SD-22 · 3.1.23 Registrar diagnóstico — SD-23 · 3.1.24 Registrar tratamiento — SD-24 · 3.1.25 Registrar receta — SD-25 |
| Clases (sección 4.8) | Mascota, AtencionMedica, Diagnostico, Tratamiento, Receta |
| Tablas (sección 4.8) | mascota, atencion_medica, diagnostico, tratamiento, receta |
| Diccionario de datos | 4.7.7 `atencion_medica` · 4.7.8 `diagnostico` · 4.7.9 `tratamiento` · 4.7.10 `receta` |

**Regla de este plan:** se implementa exactamente lo que define la Iteración 5 (requisitos, plantillas, interfaces, secuencias y diccionario). No se agregan pantallas, campos, mensajes ni reglas, y no se omite ninguno. Donde el documento no define algo necesario para que funcione, se marca en la sección 6 para que el responsable lo resuelva.

## 1. Requisitos y casos de uso

| Ref. | Función | Categoría | Caso de uso |
|---|---|---|---|
| R8.1.1 | Buscar historial de la mascota | Evidente | 3.1.22 |
| R8.1.2 | Mostrar diagnósticos y tratamientos previos | Evidente | 3.1.22 |
| R8.1.3 | Mostrar mensaje "Sin historial previo" si la mascota no tiene registros | Evidente | 3.1.22 |
| R8.2.1 | Ingresar diagnóstico | Evidente | 3.1.23 |
| R8.2.2 | Validar que el campo no esté vacío | Oculto | 3.1.23 |
| R8.2.3 | Mostrar mensaje "Ingrese un diagnóstico" si el campo está vacío | Evidente | 3.1.23 |
| R8.2.4 | Asociar diagnóstico a la mascota y fecha de atención | Oculto | 3.1.23 |
| R8.2.5 | Guardar diagnóstico en el historial | Oculto | 3.1.23 |
| R8.3.1 | Ingresar tratamiento indicado | Evidente | 3.1.24 |
| R8.3.2 | Validar que el campo no esté vacío | Oculto | 3.1.24 |
| R8.3.3 | Mostrar mensaje "Ingrese un tratamiento" si el campo está vacío | Evidente | 3.1.24 |
| R8.3.4 | Asociar tratamiento al diagnóstico | Oculto | 3.1.24 |
| R8.3.5 | Guardar tratamiento en el historial | Oculto | 3.1.24 |
| R8.4.1 | Ingresar medicamento y dosis | Evidente | 3.1.25 |
| R8.4.2 | Validar que los campos no estén vacíos | Oculto | 3.1.25 |
| R8.4.3 | Mostrar mensaje "Complete medicamento y dosis" si falta algún dato | Evidente | 3.1.25 |
| R8.4.4 | Guardar receta en el historial | Oculto | 3.1.25 |

## 2. Criterios de aceptación

Cada criterio sale de la plantilla del caso de uso y de su diagrama de secuencia. Los mensajes son los del documento.

| ID | Origen | Criterio verificable | Mensaje del documento |
|---|---|---|---|
| CA-01 | 3.1.22 pasos 1–2b · SD-22 | Al buscar la mascota, el sistema muestra su historial: fecha, atención, diagnóstico, tratamiento y receta. | "Historial clínico de la mascota" (SD-22, paso 14) |
| CA-02 | 3.1.22 curso 2c · SD-22 alt | Si la mascota no tiene registros, se muestra el aviso, se conserva el criterio de búsqueda y no se muestran filas. La consulta no modifica registros. | "Sin historial previo" |
| CA-03 | 3.1.23 curso 4b · SD-23 alt | Si el diagnóstico está vacío no se guarda y se conservan los datos. | "Ingrese un diagnóstico" |
| CA-04 | 3.1.23 pasos 4a–6 · SD-23 | Con texto, el diagnóstico queda asociado a la atención de la mascota y a su fecha, y se guarda en el historial. | "Diagnóstico guardado" |
| CA-05 | 3.1.24 curso 4b · SD-24 alt | Si el tratamiento indicado está vacío no se guarda y se conservan los datos. | "Ingrese un tratamiento" |
| CA-06 | 3.1.24 pasos 4a–6 · SD-24 | Con texto, el tratamiento queda asociado al diagnóstico y se guarda en el historial. | "Tratamiento guardado" |
| CA-07 | 3.1.25 curso 4b · SD-25 alt | Si falta medicamento o dosis no se guarda y se conservan los datos. | "Complete medicamento y dosis" |
| CA-08 | 3.1.25 pasos 4a–5 · SD-25 | Con medicamento y dosis, la receta se guarda en el historial de la mascota asociada a la atención. | "Receta guardada" |

## 3. Diseño

### 3.1 Datos — migración `V6__crear_atencion_medica.sql`

Tablas y columnas exactamente como el diccionario 4.7.7 a 4.7.10 (NN = obligatorio, UQ = único):

| Tabla | Columnas |
|---|---|
| `atencion_medica` | `id_atencion` PK · `id_cita` FK→cita, UQ, opcional · `id_mascota` NN FK→mascota · `id_veterinario` NN FK→veterinario · `fecha_hora` NN · `anamnesis` (1000) · `observaciones` (1000) |
| `diagnostico` | `id_diagnostico` PK · `id_atencion` NN FK→atencion_medica · `descripcion` NN (1000) · `fecha_registro` NN |
| `tratamiento` | `id_tratamiento` PK · `id_diagnostico` NN FK→diagnostico · `indicaciones` NN (1000) · `duracion` (60) · `fecha_inicio` · `fecha_fin` (debe ser ≥ `fecha_inicio`) |
| `receta` | `id_receta` PK · `id_atencion` NN FK→atencion_medica · `medicamento` NN (120) · `dosis` NN (60) · `frecuencia` NN (60) · `duracion` (60) · `indicaciones` (255) |

Relaciones del modelo (sección 4.6): una cita puede no tener atención y una atención puede originarse sin cita; cuando existe el vínculo es único.

### 3.2 Backend — `AtencionController` y clases de dominio

Los participantes son los de las secuencias: *Vista* → `AtencionController` → clase de dominio → datos.

| Secuencia | Operación del documento | Endpoint |
|---|---|---|
| SD-22 | `consultarHistorial(idMascota)` | `GET /api/atenciones/historial?mascotaId=` |
| SD-23 | `registrar(descripcion, atencion)` | `POST /api/atenciones/diagnosticos` (la atención llega como `idAtencion` o, por N-01, como `idCita`) |
| SD-24 | `registrar(indicaciones, diagnostico)` | `POST /api/diagnosticos/{idDiagnostico}/tratamientos` |
| SD-25 | `registrar(medicamento, dosis, atencion)` | `POST /api/atenciones/{idAtencion}/recetas` |

Consultas de apoyo para llenar los campos de selección que muestran las interfaces (atención médica, citas programadas por N-01, diagnóstico asociado): `GET /api/atenciones?mascotaId=`, `GET /api/atenciones/citas-programadas?mascotaId=` y `GET /api/atenciones/diagnosticos?mascotaId=`.

Paquete `com.hospivet.atencion`: modelos `AtencionMedica`, `Diagnostico`, `Tratamiento`, `Receta`, sus mappers, DTO y servicio. `SecurityConfig` protege `/api/atenciones/**` y `/api/diagnosticos/**` con el rol `VETERINARIO`.

### 3.3 Frontend — interfaces de la Iteración 5

La opción *Atención médica* de la navegación (ya presente en la cabecera) se habilita. Sus cuatro operaciones se acceden igual que en los demás módulos, como navegación interna, sin datos ni reglas adicionales (la Iteración 5 trata la navegación como "no corresponde a un CU específico").

| Interfaz | Campos y acciones (tal cual el mockup) |
|---|---|
| Consultar historial | Buscar mascota (`Firulais — Ana Pérez`); *Buscar historial*; *Volver a la ficha*; tabla Fecha · Atención · Diagnóstico · Tratamiento · Receta; estado "Sin historial previo" |
| Registrar diagnóstico | Mascota; Atención médica (`AT-00124 — 30/09/2026`); Diagnóstico; *Guardar diagnóstico*; *Cancelar* |
| Registrar tratamiento | Diagnóstico asociado (`D-00124 — Otitis leve`); Mascota; Tratamiento indicado; Duración; Fecha de inicio; Fecha de fin; *Guardar tratamiento*; *Cancelar* |
| Registrar receta | Atención y mascota (`AT-00124 — Firulais`); Medicamento; Dosis; Frecuencia; Duración; Indicaciones; *Guardar receta*; *Cancelar* |

Cada pantalla muestra sus referencias (R8.1, R8.1.1–3, etc.), la alerta roja y el mensaje bajo el campo como en los mockups, y conserva los datos ante un error. Los códigos `AT-00124` y `D-00124` aparecen en los mockups y se derivan del identificador, igual que `CITA-00001`.

## 4. Tareas

- [x] T1 Migración `V6__crear_atencion_medica.sql` según el diccionario.
- [x] T2 Modelos, mappers, DTO y `AtencionController` con las cuatro operaciones de SD-22 a SD-25 y sus consultas de apoyo.
- [x] T3 Vistas de las cuatro interfaces y habilitación de *Atención médica* en la navegación.
- [x] T4 Pruebas automatizadas de apoyo del backend.
- [x] T5 `docs/trazabilidad`: requisitos implementados de R8.
- [x] T6 Verificación previa de `pruebas.md`.

## 5. Lo que no se implementa

Todo lo que no figura en R8.1–R8.4: edición o eliminación de registros clínicos, impresión de recetas, relación con inventario o facturación, vacunas.

## 6. Puntos que el documento no define

**Resueltos por el responsable el 2026-10-22: opción A en todos.**

| ID | Qué falta en el documento | Opciones |
|---|---|---|
| N-01 | **Cómo nace la atención médica.** Registrar diagnóstico exige "una atención activa" y su interfaz la elige (`AT-00124`), pero ningún caso de uso ni interfaz la crea. | **A)** En *Registrar diagnóstico*, el campo *Atención médica* lista las atenciones de la mascota y sus citas programadas; al guardar sobre una cita, el sistema crea la atención de esa cita (R8.2.4 "asociar … fecha de atención"). **B)** Otra forma que indiques. |
| N-02 | Con la opción A: ¿la cita atendida cambia a `ATENDIDA`? El estado existe en el diccionario 4.7.6, pero ningún requisito lo asigna. | **A)** Sí. **B)** No, la cita queda como está. |
| N-03 | **Frecuencia vacía en la receta.** El diccionario 4.7.10 la declara obligatoria, pero R8.4 solo valida medicamento y dosis y no tiene mensaje para ella. | **A)** Obligatoria, mostrando el único mensaje del documento: "Complete medicamento y dosis". **B)** Opcional en la pantalla y en la base (se aparta del diccionario). |
| N-04 | **Fecha de fin anterior a la de inicio.** El diccionario 4.7.9 exige `fecha_fin ≥ fecha_inicio`, pero no hay mensaje. | **A)** El calendario de *Fecha de fin* no permite elegir días anteriores a la fecha de inicio (sin mensaje nuevo) y la base lo impide. **B)** Otra forma que indiques. |
| N-05 | **Destino de *Volver a la ficha*.** El botón está en el mockup, pero no existe una ficha de mascota. | **A)** Volver a la pantalla *Registrar mascota*. **B)** Otro destino que indiques. |
| N-06 | **Texto de la columna *Atención* del historial** (ejemplos "Control clínico", "Control anual"). `atencion_medica` no tiene ese campo. | **A)** El motivo de la cita de origen (`cita.motivo`). **B)** Otro dato que indiques. |
