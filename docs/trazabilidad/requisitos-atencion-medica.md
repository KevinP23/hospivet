# Trazabilidad del módulo de atención médica

Fuente: Iteración 5 integrada, casos 3.1.22 a 3.1.25 (R8.1 a R8.4), secuencias SD-22 a SD-25 y diccionario 4.7.7 a 4.7.10. Los documentos Word no se modifican. Plan y pruebas: `docs/modulos/R08-atencion-medica`.

## Requisitos implementados

| Requisito | Caso de uso / secuencia | Implementación |
|---|---|---|
| R8.1, R8.1.1–3 | 3.1.22 Consultar historial · SD-22 `consultarHistorial(idMascota)` | `GET /api/atenciones/historial`; interfaz *Consultar historial*: buscar mascota, tabla Fecha · Atención · Diagnóstico · Tratamiento · Receta, estado "Sin historial previo". |
| R8.2, R8.2.1–5 | 3.1.23 Registrar diagnóstico · SD-23 `registrar(descripcion, atencion)` | `POST /api/atenciones/diagnosticos`; mensaje "Ingrese un diagnóstico"; respuesta "Diagnóstico guardado". |
| R8.3, R8.3.1–5 | 3.1.24 Registrar tratamiento · SD-24 `registrar(indicaciones, diagnostico)` | `POST /api/diagnosticos/{id}/tratamientos`; mensaje "Ingrese un tratamiento"; respuesta "Tratamiento guardado". |
| R8.4, R8.4.1–4 | 3.1.25 Registrar receta · SD-25 `registrar(medicamento, dosis, atencion)` | `POST /api/atenciones/{id}/recetas`; mensaje "Complete medicamento y dosis"; respuesta "Receta guardada". |

## Correspondencia con el modelo de datos

Migración `V6__crear_atencion_medica.sql`, con las tablas y columnas del diccionario:

- `atencion_medica`: `id_cita` único y opcional; `id_mascota`, `id_veterinario` y `fecha_hora` obligatorios; `anamnesis` y `observaciones` opcionales.
- `diagnostico`: `id_atencion`, `descripcion` y `fecha_registro` obligatorios.
- `tratamiento`: `id_diagnostico` e `indicaciones` obligatorios; `fecha_fin ≥ fecha_inicio`.
- `receta`: `id_atencion`, `medicamento`, `dosis` y `frecuencia` obligatorios.

Relaciones: `mascota` 1:N `atencion_medica`; `veterinario` 1:N `atencion_medica`; `cita` 1:0..1 `atencion_medica`; `atencion_medica` 1:N `diagnostico` y `receta`; `diagnostico` 1:N `tratamiento`.

## Puntos que el documento no definía (resueltos por el responsable el 2026-10-22)

| ID | Decisión |
|---|---|
| N-01 | En *Registrar diagnóstico*, el campo *Atención médica* lista las atenciones de la mascota y sus citas programadas; al guardar sobre una cita se crea la atención de esa cita. |
| N-02 | La cita que origina la atención pasa a estado `ATENDIDA`. |
| N-03 | La frecuencia de la receta es obligatoria (diccionario 4.7.10) y usa el mensaje "Complete medicamento y dosis". |
| N-04 | El calendario de *Fecha de fin* no permite días anteriores a la fecha de inicio y la base lo impide. |
| N-05 | *Volver a la ficha* abre *Registrar mascota*. |
| N-06 | La columna *Atención* del historial muestra el motivo de la cita de origen. |

## Correspondencia técnica

- Paquete `com.hospivet.atencion`: `AtencionController`, `AtencionService`, modelos `AtencionMedica`, `Diagnostico`, `Tratamiento`, `Receta` y sus mappers.
- Interfaz: ruta `/atencion`, vista `AtencionView.vue`, opción *Atención médica* habilitada en la navegación.
- Códigos visibles `AT-00001` y `D-00001`, derivados del identificador como en los mockups.
