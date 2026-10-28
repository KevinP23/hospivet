# Plan — COR-02 Menú principal

| Campo | Valor |
|---|---|
| Estado | Cerrado (pruebas aprobadas por el responsable el 2026-10-27) |
| Versión | 1 |
| Fecha | 2026-10-27 |
| Origen | Iteración 5, caso 3.1.1 Iniciar sesión (paso 6 y postcondición), SD-01 e "Interfaz del menú principal después de iniciar sesión" |
| Requisitos | R1.1 ("Permitir acceso al menú principal") · la interfaz del menú no corresponde a un caso de uso (tabla 3.2: "Navegación — no corresponde a un CU específico") |
| Módulos afectados | R01 Autenticación y navegación común |

**Regla de este plan:** se implementa exactamente lo que muestra la interfaz de la Iteración 5. No se agregan pantallas, textos ni accesos, y no se omite ninguno. Lo que el documento no define se marca en la sección 6 para que el responsable lo resuelva.

## 1. Diferencia con el documento

| Documento | Implementación actual |
|---|---|
| 3.1.1 paso 6 y postcondición: "El sistema abre el menú principal". | Al iniciar sesión se abre *Clientes*. |
| *Menú principal* en la navegación lleva a la interfaz del menú. | La opción redirige a *Clientes*; no existe la interfaz del menú. |

## 2. Criterios de aceptación

| ID | Origen | Criterio verificable |
|---|---|---|
| CA-01 | 3.1.1 paso 6 · SD-01 | Con credenciales correctas, el sistema abre el menú principal. |
| CA-02 | Interfaz del menú principal | La pantalla muestra el título "Menú principal", el texto "Selecciona el módulo con el que deseas trabajar" y siete accesos en dos columnas, en este orden: *Clientes y mascotas* ("Registrar clientes y sus mascotas"), *Citas* ("Registrar, consultar, modificar y cancelar citas"), *Atención médica* ("Historial, diagnósticos, tratamientos y recetas"), *Inventario* ("Productos, existencias y movimientos"), *Facturación* ("Revisar, editar, generar, imprimir y enviar"), *Reportes* ("Inventario, ingresos, servicios y productos") y *Promociones* ("Crear, editar, publicar y eliminar"), cada uno con el distintivo HV. |
| CA-03 | Interfaz del menú principal ("los accesos del panel dirigen a cada módulo") | Cada acceso abre su módulo y la opción *Menú principal* de la navegación vuelve a esta pantalla, que aparece marcada como activa. |
| CA-04 | Interfaz del menú principal ("conserva la sesión activa") | Al ir y volver entre el menú y los módulos no se pide iniciar sesión de nuevo. |

## 3. Diseño

- **Frontend:** vista nueva `views/menu/MenuPrincipalView.vue` en la ruta `/` (hoy redirige a `/clientes`). `LoginView` navega al menú al iniciar sesión. La barra de navegación no cambia.
- **Backend (N-02):** `AuthResponse` agrega `nombre` y `apellido` del veterinario; `POST /api/auth/login` y `GET /api/auth/me` los devuelven y `AppHeader` muestra "Nombre Apellido · Veterinaria".

## 4. Tareas

- [x] T1 Vista del menú principal con los textos y el orden del mockup.
- [x] T2 Ruta `/` y navegación al menú tras iniciar sesión.
- [x] T3 Decisiones N-01 a N-03.
- [x] T4 Actualizar el plan y las pruebas de R01 (CA de inicio de sesión) y `docs/trazabilidad`.
- [x] T5 Verificación previa de `pruebas.md`.

## 5. Lo que no se implementa

Accesos para el cliente (R10, que tendrá su propio módulo), contadores, avisos o resúmenes en el menú: el mockup no los muestra.

## 6. Puntos que el documento no define

**Decisión del responsable (2026-10-27): opción A en N-01 a N-03.**

| ID | Qué falta en el documento | Opciones |
|---|---|---|
| N-01 | **Acceso *Reportes* mientras R06 no exista.** El mockup lo muestra, pero el módulo aún no está construido; en la navegación *Reportes* aparece deshabilitado. | **A)** Se muestra igual que en el mockup pero deshabilitado, como en la navegación, y se habilita al construir R06. **B)** Se oculta hasta construir R06. |
| N-02 | **Nombre en la cabecera.** El mockup muestra "Dra. Ana Ibáñez · Veterinaria"; hoy la cabecera muestra el usuario ("kevin · Veterinaria"). El sistema no guarda títulos como "Dra.". | **A)** Mostrar nombre y apellido del veterinario ("Kevin Pinto Carrasco · Veterinaria"); la sesión pasa a devolver esos dos datos. **B)** Mantener el usuario. |
| N-03 | **Destino de *Clientes y mascotas*.** Un solo acceso agrupa dos módulos que en la navegación están separados. | **A)** Abre *Clientes* (Registrar cliente); *Mascotas* sigue en la navegación. **B)** Abre *Mascotas*. |
