# Pruebas manuales — COR-02 Menú principal

Entorno: backend en `http://localhost:8080`, frontend en `http://localhost:5173/`, cuenta veterinaria activa.

La verificación previa se hizo con una base H2 temporal en memoria y una cuenta veterinaria de prueba creada solo para esa sesión, en puertos aparte (8081 y 5174) para no interrumpir los servidores del responsable.

Cada caso corresponde a un criterio del plan. Los casos CP-COR02-05 a CP-COR02-07 verifican las decisiones N-01 a N-03 (opción A, D-18).

## Resumen

| Caso | Criterio | Origen | Verificación previa | Revisión del responsable |
|---|---|---|---|---|
| CP-COR02-01 | CA-01 | 3.1.1 paso 6 / SD-01 | Correcto | Aprobado |
| CP-COR02-02 | CA-02 | Interfaz del menú principal | Correcto | Aprobado |
| CP-COR02-03 | CA-03 | Interfaz del menú principal | Correcto | Aprobado |
| CP-COR02-04 | CA-04 | Interfaz del menú principal | Correcto | Aprobado |
| CP-COR02-05 | N-01 | Interfaz del menú principal | Correcto | Aprobado |
| CP-COR02-06 | N-02 | Cabecera | Correcto | Aprobado |
| CP-COR02-07 | N-03 | Interfaz del menú principal | Correcto | Aprobado |

## Casos

### CP-COR02-01 · Abrir el menú al iniciar sesión
- **Requisito / criterio:** R1.1 / CA-01
- **Pasos:** 1. Cerrar sesión. 2. Iniciar sesión con usuario y contraseña correctos.
- **Resultado esperado:** se abre el *Menú principal*, no *Clientes*.
- **Verificación previa:** 2026-10-27 · Correcto · con la cuenta de prueba, *Ingresar* abre el *Menú principal* (ruta `/`), no *Clientes*. Con sesión iniciada, abrir `/login` también lleva al menú.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-COR02-02 · Contenido del menú
- **Requisito / criterio:** Interfaz del menú principal / CA-02
- **Pasos:** 1. Comparar la pantalla con el mockup de la Iteración 5.
- **Resultado esperado:** título, texto y los siete accesos con sus descripciones, en el mismo orden, en dos columnas y con el distintivo HV.
- **Verificación previa:** 2026-10-27 · Correcto · título, texto y los siete accesos con sus descripciones en el orden del mockup, en dos columnas y con el distintivo HV; en pantalla angosta pasan a una columna sin desplazamiento horizontal.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-COR02-03 · Accesos a los módulos
- **Requisito / criterio:** Interfaz del menú principal / CA-03
- **Pasos:** 1. Pulsar cada acceso. 2. Volver con *Menú principal* en la navegación.
- **Resultado esperado:** cada acceso abre su módulo; *Menú principal* regresa a esta pantalla y aparece marcado como activo.
- **Verificación previa:** 2026-10-27 · Correcto · *Clientes y mascotas* → Registrar cliente, *Citas* → Consultar citas del veterinario, *Atención médica* → Consultar historial, *Inventario* → Consultar inventario, *Facturación* → Gestión de facturación, *Promociones* → Gestión de promociones; *Menú principal* vuelve al menú y queda marcado como activo.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-COR02-04 · Sesión activa
- **Requisito / criterio:** Interfaz del menú principal / CA-04
- **Pasos:** 1. Entrar a varios módulos desde el menú y volver al menú.
- **Resultado esperado:** no se pide iniciar sesión de nuevo.
- **Verificación previa:** 2026-10-27 · Correcto · en todo el recorrido entre el menú y los seis módulos no se pidió iniciar sesión.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-COR02-05 · Reportes deshabilitado
- **Requisito / criterio:** N-01 (D-18)
- **Pasos:** 1. Pasar el cursor sobre *Reportes* y pulsarlo.
- **Resultado esperado:** el acceso se ve como en el mockup, con el cursor de no permitido y el aviso "Reportes se implementará en otra iteración"; no abre nada.
- **Verificación previa:** 2026-10-27 · Correcto · *Reportes* tiene el aspecto del mockup, cursor de no permitido y el aviso "Reportes se implementará en otra iteración"; al pulsarlo no sale del menú.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-COR02-06 · Nombre en la cabecera
- **Requisito / criterio:** N-02 (D-18)
- **Pasos:** 1. Iniciar sesión. 2. Revisar la cabecera. 3. Recargar la página.
- **Resultado esperado:** la cabecera muestra nombre y apellido del veterinario seguidos de "· Veterinaria" (por ejemplo "Kevin Pinto Carrasco · Veterinaria") y lo conserva al recargar.
- **Verificación previa:** 2026-10-27 · Correcto · la cabecera muestra "Prueba Verificacion · Veterinaria" (nombre y apellido de la cuenta de prueba) y lo conserva al recargar la página.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**

### CP-COR02-07 · Clientes y mascotas
- **Requisito / criterio:** N-03 (D-18)
- **Pasos:** 1. Pulsar *Clientes y mascotas*.
- **Resultado esperado:** se abre *Registrar cliente*; *Mascotas* sigue disponible en la navegación.
- **Verificación previa:** 2026-10-27 · Correcto · *Clientes y mascotas* abre *Registrar cliente*; *Mascotas* sigue en la navegación.
- **Revisión del responsable:** Aprobado (2026-10-27)
- **Observaciones (funcionamiento y visual):**
