# Actualizaciones pendientes en las iteraciones 4 y 5

Este archivo identifica correcciones documentales; no modifica los Word originales.

## Requisitos y reglas

1. Sustituir la edad almacenada de la mascota por `fecha_nacimiento` y aclarar que la edad se calcula en la fecha de consulta.
2. Unificar R3.1 y sus flujos alternativos con esa decisión: fecha futura inválida y peso mayor que cero.
3. Hacer explícito `apellido` como campo obligatorio del registro de cliente.
4. Aclarar que el inicio de sesión de esta entrega corresponde al veterinario. Si se conserva el rol `CLIENTE` en el modelo, marcar su acceso como alcance futuro.
5. Documentar cómo se aprovisiona la primera cuenta veterinaria o añadir un caso de uso administrativo futuro para crearla.
6. Confirmar expresamente si la fecha de nacimiento de la mascota debe ser obligatoria. La implementación actual no inventa esa obligatoriedad.

## Formularios y mockups

1. Formulario de cliente: añadir `Apellido` y marcar CI, nombre, apellido y teléfono como obligatorios.
2. Formulario de mascota: reemplazar `Edad` por `Fecha de nacimiento`, mantener el selector de propietario y mostrar la edad calculada solo como dato derivado.
3. Formulario de inicio de sesión: rotular el acceso como veterinario mientras ese sea el único actor habilitado.
4. En los tres formularios, representar que los valores se conservan cuando ocurre un error y que los mensajes aparecen junto al campo o en una alerta general.

## Diagramas UML

1. Diagrama de clases: `Mascota` debe tener `fechaNacimiento: LocalDate`; `edad` puede figurar como propiedad derivada (`/edad`) o método de cálculo, no como atributo persistido.
2. Diagrama de clases: `Cliente` debe incluir `apellido` obligatorio.
3. Diagrama de secuencia SD-01: reflejar búsqueda de usuario, verificación BCrypt, creación de sesión solo al éxito y los retornos “Usuario no encontrado” / “Contraseña incorrecta”.

## Inventario

1. Los mockups de Iteración 5 permiten buscar productos por nombre o código, pero el diccionario de Iteración 4 no incluye `codigo` en `Producto`. La implementación usa un código derivado `PRD-000001`; ambos documentos deben unificar esta decisión.
2. La eliminación de producto se implementa como baja lógica mediante `activo=false` para conservar movimientos y trazabilidad. Los diagramas y la plantilla R4.3 deben indicarlo expresamente.
3. Actualizar stock genera un registro `MovimientoInventario` de tipo `AJUSTE` con la diferencia absoluta y el veterinario que realizó la operación.
4. Diagrama de secuencia SD-03: incluir validación de CI único y la conservación del formulario al fallar.
5. Diagrama de secuencia SD-04: validar propietario activo, fecha no futura y peso positivo antes de persistir.
6. Aclarar que los precios del inventario se registran en bolivianos (Bs.) y pueden variar; los productos de demostración de la migración V4 no constituyen un catálogo definitivo.

## Diagrama entidad–relación y diccionario de datos

1. Mantener entidades y relaciones separadas de las clases de software: `usuario`, `veterinario`, `cliente` y `mascota` son tablas; controladores, servicios y DTO no pertenecen al DER.
2. Relación `usuario` 1:0..1 `veterinario`, compartiendo la clave del veterinario con el usuario.
3. Relación opcional `usuario` 1:0..1 `cliente`, reservada para un acceso futuro del cliente.
4. Relación `cliente` 1:N `mascota`; `mascota.id_cliente` es clave foránea obligatoria.
5. Actualizar el diccionario desde las columnas reales de la migración `V1__crear_usuarios_clientes_mascotas.sql`, incluida `fecha_nacimiento`, y distinguir claves primarias, foráneas, nulabilidad, unicidad y restricciones.

## Citas y servicios

1. Añadir al diccionario las tablas `servicio` y `cita` de `V3__crear_servicios_y_citas.sql`, con sus claves, restricciones y relaciones.
2. Confirmar el precio y la duración de `Consulta general`; la documentación solo muestra su nombre. La implementación usa provisionalmente `0.00 Bs` y `30 minutos`.
3. Definir un caso de uso futuro para administrar servicios si el catálogo dejará de ser fijo.
4. Aclarar que cancelar una cita cambia su estado a `CANCELADA` y no elimina el registro.
5. Representar la regla de disponibilidad: un veterinario no puede tener dos citas `PROGRAMADA` en la misma fecha y hora.
6. Aclarar que el veterinario se obtiene de la sesión y no se elige libremente en el formulario.
7. Actualizar DER, diccionario, diagrama de clases y secuencias R5.1–R5.4 para reflejar las relaciones reales entre cita, cliente, mascota, veterinario y servicio.

## Atención médica

1. Ningún caso de uso ni interfaz crea la atención médica. Documentar que, en *Registrar diagnóstico*, el campo *Atención médica* lista también las citas programadas de la mascota y que al guardar sobre una cita se crea su atención (N-01).
2. Indicar que la cita que origina una atención pasa a `ATENDIDA` (N-02).
3. Unificar R8.4 con el diccionario 4.7.10: la frecuencia es obligatoria y usa el mensaje "Complete medicamento y dosis" (N-03).
4. Representar en la interfaz *Registrar tratamiento* que la fecha de fin no admite días anteriores a la fecha de inicio (N-04).
5. Indicar que *Volver a la ficha* lleva a *Registrar mascota* (N-05) y que la columna *Atención* del historial muestra el motivo de la cita de origen (N-06).

## Promociones

1. Agregar la interfaz del listado de promociones a la que regresan *Volver al listado* de *Publicar* y *Eliminar* (N-01).
2. Unificar el campo *Aplicar a servicio o producto* (un valor en el mockup) con las tablas N:M del diccionario: admite varios y es opcional (N-02).
3. Unificar el mensaje de *Editar promoción*: la alerta es "Datos inválidos" (R9.2.4, SD-27) y la regla aparece bajo el campo (N-03).
4. Indicar en *Crear promoción* que el porcentaje solo admite valores de 0 a 100 (N-04).
5. Definir cuándo una promoción pasa a `FINALIZADA` o retirar ese estado del diccionario (N-05).
6. Indicar que *Buscar promoción* se elige de las promociones registradas por código o título (N-06).

## Facturación

1. Agregar la interfaz del listado de facturas a la que regresa *Volver al listado* de *Revisar factura* (N-01).
2. Indicar en *Generar factura* que repetir un ítem suma 1 a su cantidad y que el precio unitario sale del catálogo (N-02).
3. Registrar el precio real de *Consulta general*; hoy es Bs 80,00 de prueba (N-03).
4. Precisar en R7.3.5 qué promociones son vigentes y sobre qué ítems se aplica el porcentaje (N-04).
5. Definir cuándo una factura pasa a `ANULADA` o `PAGADA`, o retirar esos estados del diccionario (N-05).
6. Representar en *Editar factura* el selector de la línea del detalle (N-06).
7. Unificar el mensaje de *Editar factura*: la alerta es "Datos inválidos" (R7.2.4, SD-18) y la regla aparece bajo el campo (N-07).
8. Indicar en la sección 5.2 que la impresión es directa en la impresora del equipo, por la instalación local (N-08, D-16).
9. Nombrar el proveedor de correo de SD-21 (servidor SMTP) y definir el mensaje cuando el proveedor no confirma el envío; hoy se muestra "No fue posible enviar la factura" (N-09).
10. Indicar que *Actualizar correo* guarda el correo en el registro del cliente (N-10).
11. Indicar que facturar no descuenta stock (N-11).
