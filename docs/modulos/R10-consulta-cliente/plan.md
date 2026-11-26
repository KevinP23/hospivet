# Plan — R10 Consulta de información (Cliente)

| Campo | Valor |
|---|---|
| Estado | Cerrado (pruebas aprobadas por el responsable el 2026-11-25) |
| Versión | 1 |
| Fecha | 2026-11-25 |
| Requisitos | R10.1–R10.3 y sus subfunciones |
| Casos de uso y secuencias | 3.1.30 Solicitar cita del cliente — SD-30 · 3.1.31 Consultar servicios (Cliente) — SD-31 · 3.1.32 Ver promociones (Cliente) — SD-32 |
| Clases (sección 4.5) | Cliente, Cita, Mascota, Servicio, Veterinario, Promocion, Producto |
| Tablas | `usuario` (rol `CLIENTE`), `cliente.id_usuario` (4.7.3: "Cuenta de acceso del cliente", opcional), `cita`, `mascota`, `servicio`, `promocion`, `promocion_servicio`, `promocion_producto` |
| Depende de | R01 (inicio de sesión), R1.2 (recuperar contraseña), R02/R03 (clientes y mascotas), R05 (reglas de citas D-10, D-11, D-13), R09 (promociones publicadas) |
| Fuente | `Iteracion5_Hospivet.pdf`, págs. 21–22 (actor Cliente), 36–38 (casos generales del cliente), 46 (R10), 212–228 (casos 3.1.30 a 3.1.32, interfaces, estados sin resultados y SD-30 a SD-32) y 241 (diccionario de `cliente`) |

**Regla de este plan:** se implementa exactamente lo que define la Iteración 5 (requisitos, plantillas, interfaces, estados sin resultados y secuencias). No se agregan pantallas, campos, mensajes ni reglas, y no se omite ninguno.

**Indicación del docente (D-19):** lo que está en el PDF tiene que estar en las pantallas y no se puede agregar nada. En la sección 6 solo se proponen opciones que no agregan elementos a las interfaces.

## 1. Requisitos y casos de uso

| Ref. | Función | Categoría | Caso de uso |
|---|---|---|---|
| R10.1.1 | Ver horarios disponibles | Evidente | 3.1.30 |
| R10.1.2 | Seleccionar horario | Evidente | 3.1.30 |
| R10.1.3 | Seleccionar fecha | Evidente | 3.1.30 |
| R10.1.4 | Validar que el horario siga disponible | Oculto | 3.1.30 |
| R10.1.5 | Mostrar mensaje "Horario no disponible, seleccione otro" | Evidente | 3.1.30 |
| R10.1.6 | Registrar la cita seleccionada | Oculto | 3.1.30 |
| R10.2.1 | Ver listado de servicios | Evidente | 3.1.31 |
| R10.2.2 | Seleccionar servicio | Evidente | 3.1.31 |
| R10.2.3 | Ver precio del servicio | Evidente | 3.1.31 |
| R10.2.4 | Ver descripción del servicio | Evidente | 3.1.31 |
| R10.2.5 | Mostrar mensaje "No hay servicios disponibles" si la lista está vacía | Evidente | 3.1.31 |
| R10.3.1 | Ver promociones disponibles | Evidente | 3.1.32 |
| R10.3.2 | Ver detalle de la promoción | Evidente | 3.1.32 |
| R10.3.3 | Ver fecha de vigencia | Evidente | 3.1.32 |
| R10.3.4 | Seleccionar promoción | Evidente | 3.1.32 |
| R10.3.5 | Mostrar mensaje "No hay promociones vigentes" si no hay ninguna activa | Evidente | 3.1.32 |

Alcance del actor (sección 2.5): "El cliente solo podrá consultar información; no administrará registros internos." La única escritura del cliente es registrar su propia cita (R10.1.6).

## 2. Criterios de aceptación

| ID | Origen | Criterio verificable | Mensaje del documento |
|---|---|---|---|
| CA-01 | Actor Cliente (2.6) · N-01 y N-02 | Un cliente con cuenta inicia sesión y entra al *Portal Cliente*: cabecera "HOSPIVET · Portal Cliente", navegación *Reservar cita* · *Servicios* · *Promociones* y perfil "Nombre Apellido · Cliente". No ve ni puede usar las opciones del veterinario. | — |
| CA-02 | 3.1.30 pasos 1–2 · interfaz | *Solicitar cita del cliente* muestra *Mascota* (solo sus mascotas activas), *Servicio*, *Seleccionar fecha*, *Horarios disponibles* (los ocupados se ven como "HH:mm ocupado" y no se pueden elegir) y *Confirmar cita*. | — |
| CA-03 | 3.1.30 pasos 3–5 · SD-30 | Con mascota, servicio, fecha y horario libre, *Confirmar cita* registra la cita `PROGRAMADA` y la interfaz confirma la operación. | "Cita solicitada" (SD-30, pasos 14 y 16) |
| CA-04 | 3.1.30 curso 4b · SD-30 alt | Si el horario dejó de estar disponible entre la consulta y la confirmación (por ejemplo, otra persona lo tomó), no se registra nada, se conservan los datos y se puede elegir otro horario. | "Horario no disponible, seleccione otro" |
| CA-05 | 3.1.31 pasos 1–4b · interfaz | *Servicios disponibles* muestra las tarjetas de los servicios activos con nombre, descripción y precio. Al seleccionar una y pulsar *Ver servicio seleccionado*, se muestran su nombre y su descripción debajo. | — |
| CA-06 | 3.1.31 curso 2b | Sin servicios activos se muestra el mensaje y ninguna tarjeta. | "No hay servicios disponibles" |
| CA-07 | 3.1.32 pasos 1–4b · interfaz | *Promociones vigentes* muestra las promociones publicadas y vigentes hoy con título, descripción y "Vigencia: dd/mm/aaaa al dd/mm/aaaa". Al seleccionar una y pulsar *Ver detalle de la promoción*, aparece *Detalle de la promoción*. | — |
| CA-08 | 3.1.32 curso 2b | Sin promociones vigentes se muestra el mensaje y ninguna tarjeta. | "No hay promociones vigentes" |
| CA-09 | Alcance del actor (2.5) | El cliente solo ve sus propias mascotas y solo registra citas para ellas. Las rutas del veterinario le responden "sin permiso". | — |

## 3. Diseño

### 3.1 Datos

No hay tablas nuevas. Se usan `usuario` con rol `CLIENTE` (ya permitido por la restricción del diccionario 4.7.1) y `cliente.id_usuario` (4.7.3), que hoy existen pero no se usan. Si N-01 crea las cuentas desde la aplicación, no hace falta ninguna migración.

### 3.2 Backend

| Secuencia | Operación del documento | Endpoint (rol `CLIENTE`) |
|---|---|---|
| SD-30 | horarios disponibles (paso 2) | `GET /api/portal/citas/horarios?fecha=AAAA-MM-DD&idServicio=…` |
| SD-30 | `registrar(fecha, horario, mascota)` en `CitasClienteController` | `POST /api/portal/citas` |
| SD-31 | listado de servicios en `CatalogoController` | `GET /api/portal/servicios` |
| SD-32 | promociones vigentes en `CatalogoController` | `GET /api/portal/promociones` |
| — | mascotas del cliente para el campo *Mascota* | `GET /api/portal/mascotas` |

- **Inicio de sesión (N-02):** `AuthService` deja de rechazar el rol `CLIENTE`. La respuesta incluye el rol y el nombre del cliente. La decisión D-04 se reemplaza.
- **Seguridad:** `/api/portal/**` solo para `CLIENTE`. Las rutas actuales siguen solo para `VETERINARIO`. El cliente se identifica por la sesión (`cliente.id_usuario`), nunca por un dato enviado desde la pantalla.
- **Registro de la cita:** reutiliza las reglas de R05: fecha y hora futuras (D-10), solapamiento según la duración del servicio (D-11) y bloqueo de la agenda del veterinario durante la transacción (D-13). Estado `PROGRAMADA`; veterinario según N-04; motivo según N-05.
- **Horarios (N-03):** se generan desde el horario de atención configurado. Un horario está ocupado si se solapa con una cita `PROGRAMADA` del veterinario (D-11).
- Paquete `com.hospivet.portal`: `CitasClienteController`, `CatalogoController`, `PortalService` y sus DTO. Reutiliza `CitaService`, `ServicioMapper`, `PromocionMapper` y `MascotaMapper`.

### 3.3 Frontend — interfaces de la Iteración 5

- Al iniciar sesión, el veterinario sigue yendo al menú principal y el cliente va a `/portal/cita` (*Reservar cita*).
- Diseño propio del portal, como los mockups: colores azules, cabecera "HOSPIVET · Portal Cliente", navegación de tres opciones y perfil "Nombre Apellido · Cliente" con *Cerrar sesión*, igual que el veterinario.

| Interfaz | Elementos (tal cual el mockup) | Estado sin resultados |
|---|---|---|
| Solicitar cita del cliente (`/portal/cita`) | Título y "Referencias: R10.1, R10.1.1–6"; alerta roja "Horario no disponible, seleccione otro"; MASCOTA; SERVICIO; SELECCIONAR FECHA; HORARIOS DISPONIBLES (botones "09:00", "10:00 ocupado"…); *Confirmar cita* | — |
| Servicios disponibles (`/portal/servicios`) | Título y referencias; tarjetas con nombre, descripción y "Bs 80,00"; nombre y descripción del seleccionado; *Ver servicio seleccionado* | "No hay servicios disponibles" |
| Promociones vigentes (`/portal/promociones`) | Título y referencias; tarjetas con título, descripción y "Vigencia: 01/10/2026 al 31/10/2026"; *Ver detalle de la promoción*; "Detalle de la promoción" con su texto | "No hay promociones vigentes" |

## 4. Tareas

- [x] T1 Cuentas de cliente según N-01 e inicio de sesión del rol `CLIENTE` (N-02); actualizar la prueba existente que hoy espera el rechazo del rol `CLIENTE`.
- [x] T2 `PortalService`, `CitasClienteController` y `CatalogoController` (SD-30 a SD-32) con la seguridad del rol `CLIENTE`.
- [x] T3 Diseño del Portal Cliente y sus tres interfaces, con los estados sin resultados.
- [x] T4 Pruebas automatizadas de apoyo del backend: acceso por rol, horarios y ocupados, registro y conflicto, servicios, promociones vigentes y aislamiento entre clientes.
- [x] T5 Datos de prueba: cuenta local de María Elena Vargas Ríos y alta real del cliente 5340981 probada por el responsable (2026-11-25).
- [x] T6 `docs/trazabilidad`: requisitos implementados, decisión que reemplaza D-04, actualizaciones pendientes del documento; verificación previa de `pruebas.md`.

## 5. Lo que no se implementa

Todo lo que no figura en R10: que el cliente vea, modifique o cancele sus citas; registro de clientes por sí mismos con una pantalla de "crear cuenta"; pagos en línea (limitación 2.5); facturas del cliente; reservar en nombre de otro cliente; elegir veterinario.

## 6. Puntos que el documento no define

| ID | Qué falta en el documento | Opciones |
|---|---|---|
| N-01 | **Cómo obtiene el cliente su cuenta.** El diccionario prevé `cliente.id_usuario` ("Cuenta de acceso del cliente"), pero ningún caso de uso ni pantalla crea esa cuenta. | **A)** Sin pantallas nuevas, con *Recuperar contraseña*. El usuario del cliente es su **CI**. Si un cliente activo con correo escribe su CI o su correo en *Recuperar contraseña*, el sistema crea su cuenta (si no existe) y le envía la contraseña temporal, igual que a un veterinario. Esto amplía N-01 de R1.2 (D-20), que hoy solo acepta veterinarios. **B)** Las cuentas se crean solo con un script en la base local, para la demostración; la aplicación no las crea. |
| N-02 | **Por dónde entra el cliente.** *Iniciar sesión* nombra solo a los veterinarios, y D-04 deja el rol `CLIENTE` para después. | **A)** La misma pantalla *Iniciar sesión*: según el rol abre el menú del veterinario o el *Portal Cliente*. **B)** Otra pantalla de inicio de sesión para clientes; agregaría una pantalla que no está en el PDF. |
| N-03 | **Horarios configurados.** La precondición dice "existen horarios configurados", pero no hay tabla ni pantalla de horarios. El mockup muestra ejemplos como 09:00, 10:00, 10:30 y 15:00. | **A)** Horario de atención fijo en la configuración del backend: lunes a sábado, de 09:00 a 12:00 y de 15:00 a 18:00, cada 30 minutos. El calendario no permite elegir días pasados ni domingos; hoy solo muestra los horarios que aún no pasaron. **B)** Otro horario que indiques. |
| N-04 | **Qué veterinario atiende.** La cita necesita un veterinario, y el mockup no tiene ese campo. | **A)** El sistema asigna un veterinario activo libre en ese horario (hoy hay uno). Un horario se ve "ocupado" solo si ningún veterinario está libre. **B)** El cliente elige el veterinario; agregaría un campo. |
| N-05 | **Motivo de la cita.** Es obligatorio (D-12), pero el mockup no tiene ese campo. | **A)** Se guarda automáticamente "Solicitada por el cliente". **B)** Agregar el campo *Motivo*; agregaría un elemento. |
| N-06 | **Cómo se muestra el resultado correcto.** El mockup solo muestra el error; SD-30 termina con "Cita solicitada". | **A)** Alerta verde "Cita solicitada" (texto de SD-30) en el lugar del mensaje de error; el horario elegido pasa a "ocupado". **B)** Volver a la pantalla vacía sin mensaje. |
| N-07 | **Descripción del servicio.** El mockup muestra un texto corto en la tarjeta y uno más largo debajo, pero `servicio` tiene una sola descripción. | **A)** La misma descripción en la tarjeta y debajo. Al abrir, el primer servicio aparece seleccionado con su detalle, como en el mockup; al hacer clic en otra tarjeta y pulsar *Ver servicio seleccionado*, cambia el detalle. **B)** Agregar a `servicio` una segunda descripción (corta) con una migración y llenarla. |
| N-08 | **Qué promociones y qué detalle.** "Vigentes" no está definido para el cliente, y el texto del detalle ("Aplicable al servicio de vacunación. Descuento: 20 %.") no es un campo. | **A)** Solo promociones `PUBLICADA` con hoy dentro de su vigencia. El detalle se arma así: "Aplicable a: Vacunación antirrábica, … Descuento: 20 %."; si no tiene ítems, "Aplicable a todos los servicios y productos. Descuento: 20 %." Al abrir, la primera aparece seleccionada con su detalle. **B)** Mostrar también las que empiezan después de hoy. |

**Decisión del responsable (2026-11-25, D-21): N-01 A, N-02 A, N-03 A, N-04 A, N-05 A, N-06 A, N-07 A y N-08 A.**

**Ajuste del responsable (2026-11-25, después de probar el alta con su correo):** el usuario del cliente ya no es el CI, sino el primer nombre + la inicial del primer apellido + los últimos 4 dígitos del CI, en minúsculas y sin tildes (por ejemplo, `mariav4562`); si ese usuario ya existe, se usa el CI completo (`mariav7134562`). En *Recuperar contraseña* el cliente puede escribir su usuario, su CI o su correo. La contraseña temporal pasa a 8 caracteres (también para el veterinario, R1.2).

## 7. Riesgos

- **Cambio en módulos cerrados:** N-01 A amplía R1.2 a clientes y N-02 A reemplaza D-04 en R01. Se registra en `decisiones.md`, y la prueba que hoy espera el rechazo del cliente se actualiza.
- **Clientes sin correo:** con N-01 A, un cliente sin correo registrado no puede obtener su cuenta hasta que el veterinario agregue el correo. Hoy no hay pantalla para editar clientes, y eso queda fuera de alcance.
- **Seguridad:** un cliente nunca debe ver datos de otro. Todas las consultas del portal se filtran por el cliente de la sesión, y hay pruebas automáticas para eso.
- **Commits pendientes:** los cambios de R1.2 todavía no se commitearon. Conviene subirlos antes de codificar R10, para no mezclar módulos.
