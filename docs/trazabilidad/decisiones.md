# Decisiones de implementación

Estas decisiones se aplican a la primera entrega de HOSPIVET. Los documentos Word originales de las iteraciones 4 y 5 se conservan sin cambios.

| ID | Decisión | Motivo y efecto |
|---|---|---|
| D-01 | La iteración 5 integrada es la referencia funcional principal; la iteración 4 se usa para contrastar el modelo. | Evita mezclar versiones sin perder trazabilidad con el diseño anterior. |
| D-02 | La mascota almacena `fecha_nacimiento` y la edad se calcula al consultar. | La edad cambia con el tiempo; no debe persistirse como dato fijo. |
| D-03 | `apellido` es obligatorio al registrar un cliente. | El modelo de datos lo declara obligatorio y permite identificar al propietario con nombre completo. |
| D-04 | En esta entrega solo inicia sesión el rol `VETERINARIO`. | Es el actor indicado para registrar clientes y mascotas. El rol `CLIENTE` permanece reservado para una entrega posterior. |
| D-05 | La cuenta veterinaria inicial se crea una sola vez mediante variables de entorno. | Permite iniciar el sistema sin guardar usuarios o contraseñas reales en el repositorio. |
| D-06 | La autenticación usa sesión HTTP y contraseña BCrypt. | El navegador conserva una sesión segura y nunca se almacena la contraseña en texto plano. |
| D-07 | PostgreSQL es la base de producción/desarrollo y Flyway administra el esquema. | La creación de tablas queda versionada y repetible. H2 se usa únicamente en pruebas automatizadas. |
| D-08 | Los formularios solo se limpian después de una respuesta exitosa. | Ante validaciones o errores del servidor se mantienen todos los valores ingresados. |
| D-09 | Backend y frontend son módulos separados bajo una sola carpeta de proyecto. | Facilita ejecutar Spring Boot y Vue de forma independiente durante el desarrollo. |
| D-10 | Las citas solo se registran o modifican con fecha y hora posteriores al momento actual. | Decisión del responsable (auditoría AUD-O1). Mensaje: `Seleccione una fecha y hora futuras`. |
| D-11 | Un horario está ocupado si se solapa con la duración del servicio de otra cita `PROGRAMADA` del mismo veterinario. | Decisión del responsable (AUD-O2). Usa `servicio.duracion_min`; las citas canceladas liberan el horario. |
| D-12 | El motivo de la cita es obligatorio. | Decisión del responsable (AUD-O3). Difiere del diccionario 4.7.6, que lo deja opcional. |
| D-13 | El registro y la modificación de citas bloquean la fila del veterinario durante la transacción. | Evita que dos solicitudes simultáneas ocupen el mismo horario; un índice parcial no cubre solapamientos y H2 no lo admite. |
| D-14 | Atención médica: puntos N-01 a N-06 del plan R08 resueltos con la opción A (la atención nace de la cita al registrar el diagnóstico, la cita pasa a `ATENDIDA`, frecuencia obligatoria, fecha de fin no anterior a la de inicio, *Volver a la ficha* abre *Registrar mascota* y el historial muestra el motivo de la cita). | Decisión del responsable el 2026-10-22; detalle en `requisitos-atencion-medica.md`. |
| D-15 | Promociones: puntos N-01 a N-06 del plan R09 resueltos con la opción A (listado de promociones, varios servicios y productos opcionales, "Datos inválidos" con la regla bajo el campo al editar, descuento limitado a 0–100, `FINALIZADA` sin uso y búsqueda por selector). | Decisión del responsable el 2026-10-23; detalle en `requisitos-promociones.md`. |
| D-16 | HOSPIVET se instala en la computadora de la veterinaria (servidor, base de datos e interfaz en el mismo equipo), no como sitio web publicado. | Indicación del responsable el 2026-10-27; condiciona impresión, correo y la forma de entrega. |
| D-17 | Facturación: puntos N-01 a N-11 del plan R07 resueltos con la opción A, salvo N-08 con la opción B (listado de facturas, cantidad que sube al repetir el ítem con precio del catálogo, *Consulta general* a Bs 80,00 como precio de prueba, promoción vigente aplicada a sus ítems, solo estado `EMITIDA`, edición por línea, "Datos inválidos" con la regla bajo el campo, impresión directa en la impresora del equipo, correo por SMTP, *Actualizar correo* sobre el registro del cliente y sin descuento de stock). | Decisión del responsable el 2026-10-27; detalle en `requisitos-facturacion.md`. |

## Alcance actual

- Inicio y cierre de sesión para veterinarios (R1.1).
- Registro de clientes (R2.1) y de mascotas vinculadas obligatoriamente a un cliente activo (R3.1).
- Cálculo de edad desde la fecha de nacimiento.
- Inventario: consulta, alta, actualización de stock con movimiento de auditoría y baja lógica (R4.1–R4.4).
- Citas: registro, modificación, cancelación y consulta del veterinario (R5.1–R5.4).
- Atención médica: consultar historial, registrar diagnóstico, tratamiento y receta (R8.1–R8.4).
- Promociones: crear, editar, publicar y eliminar (R9.1–R9.4).
- Facturación: revisar, editar, generar, imprimir y enviar facturas (R7.1–R7.5).
- Validaciones tanto en frontend como en backend, con errores asociados a cada campo.

Fuera de alcance por ahora: recuperar contraseña (R1.2), alta de usuarios desde la interfaz, acceso de clientes (R10), edición o baja de clientes y mascotas y reportes (R6). El estado de cada módulo está en `docs/modulos/README.md`.
