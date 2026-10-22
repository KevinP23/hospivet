# Comparación de interfaces con Iteración 5

Fuente visual revisada: `Iteracion5_Hospivet_integrada.docx`. El documento Word se mantuvo sin modificaciones.

## Criterios conservados

- Cabecera blanca con marca `HV`, nombre del sistema y perfil veterinario.
- Navegación horizontal sobre fondo verde claro y módulo activo dentro de una cápsula blanca.
- Fondo general gris claro, paneles blancos con borde fino y controles redondeados.
- Títulos, referencias, formularios de dos columnas, alertas rojas y botones principales verdes.
- Los valores introducidos se mantienen ante errores de validación o respuestas rechazadas por el backend.

## Inicio de sesión

- Campos: usuario y contraseña.
- Acción: `Ingresar`.
- Estados contemplados: `Usuario no encontrado`, `Contraseña incorrecta` y campos obligatorios.
- La recuperación de contraseña se conserva solo como referencia visual; no es interactiva porque no pertenece a la primera entrega.
- El perfil mostrado proviene de la sesión real y no del nombre ficticio utilizado en el mockup.

## Registro de cliente

- Distribución del mockup: nombre/apellido, carnet de identidad/teléfono y correo/dirección.
- Corrección aprobada aplicada: `apellido` obligatorio.
- Acciones: `Guardar cliente` y `Cancelar`.
- Estados contemplados: `Complete los campos obligatorios` y `CI inválido`.

## Registro de mascota

- Distribución del mockup: propietario/nombre, especie/raza, fecha de nacimiento/edad calculada, peso/sexo y observaciones.
- Corrección aprobada aplicada: se almacena `fechaNacimiento`; la edad solo se calcula y muestra, no se persiste.
- La mascota se vincula a un cliente existente mediante `idCliente`.
- Acciones: `Guardar mascota` y `Cancelar`.
- Estados contemplados: `Complete los campos obligatorios` y `Edad o peso inválido`.

## Elementos fuera del alcance

Atención médica, facturación, reportes y promociones aparecen en la navegación para conservar el diseño, pero permanecen deshabilitados y no constituyen módulos implementados. Inventario y citas ya están implementados; su correspondencia con las interfaces está en `requisitos-inventario.md`, `requisitos-citas.md` y en los planes de `docs/modulos`.
