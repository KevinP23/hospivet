# Trazabilidad de la consulta del cliente (R10)

Fuente: Iteración 5 (`Iteracion5_Hospivet.pdf`), casos 3.1.30 a 3.1.32 (R10.1 a R10.3), sus interfaces del *Portal Cliente*, estados sin resultados y secuencias SD-30 a SD-32. Los documentos originales no se modifican. Plan y pruebas: `docs/modulos/R10-consulta-cliente`.

Indicación del docente: las pantallas deben contener exactamente lo que muestra el PDF, sin agregados.

## Requisitos implementados

| Requisito | Caso de uso / secuencia | Implementación |
|---|---|---|
| R10.1, R10.1.1–6 | 3.1.30 Solicitar cita del cliente · SD-30 `registrar(fecha, horario, mascota)` en `CitasClienteController` | `GET /api/portal/mascotas`, `GET /api/portal/citas/horarios` y `POST /api/portal/citas`. Horarios libres y "HH:mm ocupado"; registra la cita `PROGRAMADA` y responde "Cita solicitada"; si el horario ya no está libre: "Horario no disponible, seleccione otro". |
| R10.2, R10.2.1–5 | 3.1.31 Consultar servicios · SD-31 en `CatalogoController` | `GET /api/portal/servicios`: tarjetas con nombre, descripción y precio; "No hay servicios disponibles". |
| R10.3, R10.3.1–5 | 3.1.32 Ver promociones · SD-32 en `CatalogoController` | `GET /api/portal/promociones`: promociones publicadas vigentes hoy con su vigencia y detalle; "No hay promociones vigentes". |

## Correspondencia con el modelo de datos

No hay tablas ni migraciones nuevas. Se usan `usuario` con rol `CLIENTE` (4.7.1) y `cliente.id_usuario`, la "Cuenta de acceso del cliente" (4.7.3). La cita del cliente se guarda en `cita` con las mismas reglas de R05 (D-10, D-11, D-12, D-13).

## Puntos que el documento no definía (resueltos por el responsable el 2026-11-25, D-21)

| ID | Decisión |
|---|---|
| N-01 | El usuario del cliente es nombre + inicial del apellido + últimos 4 dígitos del CI (por ejemplo, `mariav4562`); si ya existe, se usa el CI completo (ajuste pedido por el responsable el 2026-11-25; antes era el CI). La cuenta se crea al pedir *Recuperar contraseña* con su CI o correo, y la contraseña llega por correo; amplía N-01 de R1.2 (D-20). |
| N-02 | El cliente usa la misma pantalla *Iniciar sesión* y entra al *Portal Cliente*; reemplaza D-04. |
| N-03 | Horario de atención: lunes a sábado, de 09:00 a 12:00 y de 15:00 a 18:00, cada 30 minutos (configurable con `HOSPIVET_PORTAL_*`). Sin días pasados ni domingos; hoy solo se muestran los horarios que no pasaron. |
| N-04 | El sistema asigna un veterinario activo libre; un horario está "ocupado" si ninguno está libre. |
| N-05 | Motivo automático "Solicitada por el cliente". |
| N-06 | Resultado correcto en verde: "Cita solicitada" (SD-30). |
| N-07 | La misma descripción del servicio en la tarjeta y en el detalle; el primero aparece seleccionado. |
| N-08 | Solo promociones `PUBLICADA` con hoy dentro de su vigencia. El detalle es "Aplicable a: … Descuento: N %." o "Aplicable a todos los servicios y productos…". |

## Correspondencia técnica

- Paquete `com.hospivet.portal`: `CitasClienteController`, `CatalogoController`, `PortalService`, `HorarioAtencion` y los DTO. Reutiliza `CitaService.horarioLibre` (D-11) y el bloqueo de agenda (D-13).
- Seguridad: `/api/portal/**` solo para el rol `CLIENTE`; las demás rutas siguen solo para `VETERINARIO`. El cliente se obtiene siempre de la sesión.
- Inicio de sesión: `AuthService` acepta al rol `CLIENTE` con su ficha activa; `RecuperacionService` crea la cuenta del cliente.
- Interfaz: `PortalLayout.vue` (colores azules del mockup), `SolicitarCitaView.vue`, `ServiciosClienteView.vue` y `PromocionesClienteView.vue`, con las rutas `/portal/cita`, `/portal/servicios` y `/portal/promociones`. Después del login, el cliente va al portal y el veterinario al menú principal; ninguno puede abrir las pantallas del otro.
