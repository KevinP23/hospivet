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

## Alcance actual

- Inicio y cierre de sesión para veterinarios.
- Registro y listado de clientes.
- Registro y listado de mascotas vinculadas obligatoriamente a un cliente activo.
- Cálculo de edad desde la fecha de nacimiento.
- Validaciones tanto en frontend como en backend, con errores asociados a cada campo.

Fuera de alcance por ahora: alta de usuarios desde la interfaz, acceso de clientes, edición/eliminación, citas, historia clínica, vacunas, inventario y reportes.
