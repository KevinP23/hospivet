# Plan — COR-03 Pantalla de inicio de sesión igual al PDF

| Campo | Valor |
|---|---|
| Estado | Cerrado (pruebas aprobadas por el responsable el 2026-11-26) |
| Versión | 1 |
| Fecha | 2026-11-26 |
| Origen | Revisión de R01 del 2026-11-26: diferencias D1 a D4 entre la pantalla *Iniciar sesión* y su mockup |
| Requisitos | R1.1, R1.1.1–6 (sin cambios de funcionamiento) |
| Fuente | `Iteracion5_Hospivet.pdf`, págs. 52 (Interfaz Iniciar sesión) y 53 (estado "Contraseña incorrecta") |
| Módulos afectados | R01 Autenticación (pantalla de inicio de sesión) y cabecera común antes de iniciar sesión (también en *Recuperar contraseña*) |

**Regla de este plan:** indicación del docente (D-19): lo que está en el PDF tiene que estar en las pantallas y no se puede agregar nada.

## 1. Diferencias y corrección aprobada

| ID | Hoy en la aplicación | En el mockup | Corrección aprobada |
|---|---|---|---|
| D1 | La alerta roja ("Usuario no encontrado", "Contraseña incorrecta") aparece arriba de los campos. | Debajo de *Contraseña*, encima de *Ingresar*. | Mover la alerta a ese lugar. |
| D2 | Asterisco rojo delante de USUARIO y CONTRASEÑA. | Sin asteriscos. | Quitar los asteriscos. |
| D3 | Con campos vacíos aparecen "Ingrese el usuario" e "Ingrese la contraseña" bajo cada campo. | No existen esos textos. | Quitarlos: con algún campo vacío, *Ingresar* no hace nada (igual que *Recuperar contraseña*, N-05 de R1.2). |
| D4 | La cabecera muestra "Acceso veterinario" antes de iniciar sesión. | Muestra el nombre del usuario, que antes de iniciar sesión no existe; además, desde R10 también entran clientes. | No mostrar el recuadro del perfil hasta que haya sesión. |
| D5 | Aviso "Su sesión expiró, inicie sesión nuevamente" cuando vence la sesión. | No está. | **Se mantiene** (decisión del responsable): fue aprobado en COR-01 y solo aparece en ese caso. |

## 2. Criterios de aceptación

| ID | Criterio verificable |
|---|---|
| CA-01 | Con usuario inexistente o contraseña equivocada, la alerta roja aparece debajo de *Contraseña* y encima de *Ingresar*, como en el mockup. |
| CA-02 | *Usuario* y *Contraseña* no muestran asterisco. |
| CA-03 | Con algún campo vacío, *Ingresar* (o Enter) no envía nada y no aparece ningún texto bajo los campos. |
| CA-04 | Antes de iniciar sesión (en *Iniciar sesión* y en *Recuperar contraseña*) la cabecera no muestra el recuadro del perfil; con sesión iniciada sigue mostrando "Nombre Apellido · Veterinaria" o el del cliente en el portal. |
| CA-05 | El inicio de sesión sigue funcionando igual (R01, CP-R01-02 a 06). |

## 3. Cambios

- `frontend/src/views/auth/LoginView.vue`: alerta de error dentro del formulario, después de *Contraseña* (D1); `hide-required-asterisk` y sin reglas de campos obligatorios; *Ingresar* no actúa con campos vacíos (D2, D3).
- `frontend/src/components/AppHeader.vue`: sin el recuadro "Acceso veterinario" cuando no hay sesión (D4).
- Sin cambios en el backend ni en la base de datos.
- Documentación: CA-01 y CP-R01-01 de R01 pasan a "con campos vacíos *Ingresar* no hace nada"; decisión D-22; comparación con las interfaces.

## 4. Tareas

- [x] T1 Cambios en `LoginView.vue` y `AppHeader.vue`.
- [x] T2 Verificar en la aplicación los criterios CA-01 a CA-05 y que las pruebas automáticas sigan pasando.
- [x] T3 Actualizar R01 (plan y pruebas), `decisiones.md` y `comparacion-interfaces-iteracion5.md`.
