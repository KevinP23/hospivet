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
