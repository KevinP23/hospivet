# Pruebas manuales — R10 Consulta de información (Cliente)

Entorno: backend en `http://localhost:8080`, frontend en `http://127.0.0.1:5173/`, base PostgreSQL local, correo configurado (`HOSPIVET_MAIL_*`) y al menos un cliente con correo y mascotas.

Resultado de la revisión del responsable: `Aprobado` · `Observado` · `Rechazado`.

Los casos usan las opciones aprobadas en el plan (D-21), sin agregados en pantalla. Para la verificación previa se creó una cuenta de prueba local para la clienta María Elena Vargas Ríos (CI 7134562); sus datos están en `C:Tallerdatos-pruebaentas-prueba.txt`, fuera del repositorio. En todos los casos se revisa además que la pantalla tenga exactamente los elementos del mockup del PDF.

## Resumen

| Caso | Criterio | Verificación previa | Revisión del responsable |
|---|---|---|---|
| CP-R10-01 | CA-01 · N-01 | Realizada por el responsable (2026-11-25) | Aprobado (2026-11-25) |
| CP-R10-02 | CA-01 · N-02 | Correcto (2026-11-25) | Aprobado (2026-11-25) |
| CP-R10-03 | CA-02 · N-03 | Correcto (2026-11-25) | Aprobado (2026-11-25) |
| CP-R10-04 | CA-03 | Correcto (2026-11-25) | Aprobado (2026-11-25) |
| CP-R10-05 | CA-04 | Correcto (2026-11-25) | Aprobado (2026-11-25) |
| CP-R10-06 | CA-05 | Correcto (2026-11-25) | Aprobado (2026-11-25) |
| CP-R10-07 | CA-06 | Correcto (2026-11-25) | Aprobado (2026-11-25) |
| CP-R10-08 | CA-07 | Correcto (2026-11-25) | Aprobado (2026-11-25) |
| CP-R10-09 | CA-08 | Correcto (2026-11-25) | Aprobado (2026-11-25) |
| CP-R10-10 | CA-09 | Correcto (2026-11-25) | Aprobado (2026-11-25) |

## Casos

### CP-R10-01 · El cliente obtiene su cuenta

- **Requisito / criterio:** actor Cliente / CA-01 (N-01)
- **Precondición:** un cliente activo con correo y sin cuenta.
- **Pasos:**
  1. En *Recuperar contraseña*, escribir el CI del cliente y pulsar *Enviar contraseña temporal*.
  2. Revisar el correo del cliente.
- **Resultado esperado:** aparece "Recuperación solicitada" y llega el correo con el usuario (nombre + inicial del apellido + últimos 4 dígitos del CI (por ejemplo, `mariav4562`)) y la contraseña temporal de 8 caracteres.
- **Verificación previa:** 2026-11-25 · Realizada por el responsable con el cliente de prueba 5340981 y su correo real: llegó el correo con usuario y contraseña, y pudo entrar al Portal Cliente. La prueba automática con envío simulado confirma que, con el CI de la clienta, se crea la cuenta (usuario = CI, rol `CLIENTE`, vinculada en `cliente.id_usuario`) y se envía la contraseña temporal a su correo.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R10-02 · Inicio de sesión del cliente

- **Requisito / criterio:** CA-01 (N-02)
- **Precondición:** el cliente tiene su contraseña (CP-R10-01).
- **Pasos:**
  1. En *Iniciar sesión*, entrar con el usuario y la contraseña del correo.
  2. Revisar la cabecera y la navegación.
- **Resultado esperado:** se abre *Solicitar cita del cliente* en el *Portal Cliente*, con la cabecera "HOSPIVET · Portal Cliente", las opciones *Reservar cita*, *Servicios* y *Promociones*, y el perfil "Nombre Apellido · Cliente". No aparecen las opciones del veterinario.
- **Verificación previa:** 2026-11-25 · Correcto · con una cuenta de prueba local de la clienta María Elena Vargas Ríos (CI 7134562), el inicio de sesión abre *Solicitar cita del cliente* con la cabecera azul "HOSPIVET · Portal Cliente", *Reservar cita* · *Servicios* · *Promociones* y "María Elena Vargas Ríos · Cliente". *Cerrar sesión* vuelve a *Iniciar sesión*.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R10-03 · Ver horarios disponibles

- **Requisito / criterio:** R10.1.1–R10.1.3 / CA-02 (N-03)
- **Precondición:** sesión de cliente; existe una cita programada del veterinario en la fecha que se elegirá.
- **Pasos:**
  1. Elegir *Mascota* y *Servicio*.
  2. En *Seleccionar fecha*, elegir esa fecha.
- **Resultado esperado:** *Mascota* solo lista las mascotas del cliente. Aparecen los horarios de atención del día; el de la cita existente se ve como "HH:mm ocupado" y no se puede elegir. Los domingos y los días pasados no se pueden elegir.
- **Verificación previa:** 2026-11-25 · Correcto · 25/11/2026 con *Baño y corte* (60 min): 09:00, 09:30 y 10:00 aparecen "ocupado" por las citas de Nala (09:00) y Toby (10:00); los demás horarios de 09:00 a 12:00 y de 15:00 a 18:00 se pueden elegir. *Mascota* lista solo Mishi y Toby. La prueba automática confirma los 12 horarios de un lunes y que los domingos no tienen horarios.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R10-04 · Solicitar una cita

- **Requisito / criterio:** R10.1.6 / CA-03 (N-04, N-05, N-06)
- **Precondición:** CP-R10-03.
- **Pasos:**
  1. Elegir un horario libre y pulsar *Confirmar cita*.
  2. Entrar como veterinario y abrir *Citas* en esa fecha.
- **Resultado esperado:** aparece "Cita solicitada" en verde y el horario pasa a "ocupado". El veterinario ve la cita `PROGRAMADA` con la mascota, el servicio y el motivo "Solicitada por el cliente".
- **Verificación previa:** 2026-11-25 · Correcto · 10:30 → "Cita solicitada" en verde; 10:30 y 11:00 pasan a "ocupado". En la base: cita `PROGRAMADA` de Mishi, *Baño y corte*, motivo "Solicitada por el cliente", veterinario Kevin Fernando Pinto Carrasco.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R10-05 · Horario que deja de estar disponible

- **Requisito / criterio:** R10.1.4, R10.1.5 / CA-04
- **Precondición:** el cliente tiene la pantalla abierta con un horario libre a la vista.
- **Pasos:**
  1. En otra ventana, el veterinario registra una cita en ese mismo horario.
  2. El cliente elige ese horario y pulsa *Confirmar cita*.
- **Resultado esperado:** aparece "Horario no disponible, seleccione otro" en la alerta roja del mockup, no se registra nada, se conservan mascota, servicio y fecha, y el horario se ve como "ocupado".
- **Verificación previa:** 2026-11-25 · Correcto en la prueba automática: confirmar un horario tomado responde "Horario no disponible, seleccione otro" (409) sin registrar nada. En la aplicación no se simularon dos ventanas a la vez.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R10-06 · Consultar servicios

- **Requisito / criterio:** R10.2.1–R10.2.4 / CA-05 (N-07)
- **Precondición:** sesión de cliente; hay servicios activos.
- **Pasos:**
  1. Abrir *Servicios*.
  2. Hacer clic en otra tarjeta y pulsar *Ver servicio seleccionado*.
- **Resultado esperado:** tarjetas con nombre, descripción y precio ("Bs 80,00"). El primero aparece seleccionado con su detalle; al cambiar, el detalle muestra el nombre y la descripción del elegido.
- **Verificación previa:** 2026-11-25 · Correcto · 12 tarjetas con nombre, descripción y precio (por ejemplo, *Baño y corte* Bs 90,00); la primera aparece seleccionada, como el mockup, con *Ver servicio seleccionado* y el detalle debajo.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R10-07 · Sin servicios

- **Requisito / criterio:** R10.2.5 / CA-06
- **Precondición:** base sin servicios activos (prueba automática o base de prueba).
- **Pasos:**
  1. Abrir *Servicios*.
- **Resultado esperado:** "No hay servicios disponibles", como en el estado alternativo del PDF, sin tarjetas.
- **Verificación previa:** 2026-11-25 · Correcto en la prueba automática: sin servicios activos la lista llega vacía; la pantalla usa el mismo estado que el mockup alternativo.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R10-08 · Ver promociones vigentes

- **Requisito / criterio:** R10.3.1–R10.3.4 / CA-07 (N-08)
- **Precondición:** hay promociones publicadas vigentes hoy, y también borradores y promociones vencidas.
- **Pasos:**
  1. Abrir *Promociones*.
  2. Elegir otra tarjeta y pulsar *Ver detalle de la promoción*.
- **Resultado esperado:** solo aparecen las publicadas vigentes hoy, con título, descripción y "Vigencia: dd/mm/aaaa al dd/mm/aaaa". El detalle muestra a qué se aplica y el descuento.
- **Verificación previa:** 2026-11-25 · Correcto · solo *Campaña antirrábica noviembre* y *Black Friday HOSPIVET* (publicadas y vigentes hoy); el detalle dice "Aplicable a: Vacunación antirrábica. Descuento: 20 %.". La prueba automática confirma que los borradores y las vencidas no aparecen.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R10-09 · Sin promociones vigentes

- **Requisito / criterio:** R10.3.5 / CA-08
- **Precondición:** ninguna promoción publicada vigente hoy (prueba automática o base de prueba).
- **Pasos:**
  1. Abrir *Promociones*.
- **Resultado esperado:** "No hay promociones vigentes", como en el estado alternativo del PDF, sin tarjetas.
- **Verificación previa:** 2026-11-25 · Correcto en la prueba automática: sin promociones vigentes la lista llega vacía; la pantalla usa el mismo estado que el mockup alternativo.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**

### CP-R10-10 · El cliente solo ve lo suyo

- **Requisito / criterio:** alcance del actor (2.5) / CA-09
- **Precondición:** sesión de cliente.
- **Pasos:**
  1. Escribir en la barra de direcciones `/clientes` o `/facturacion`.
  2. Revisar el campo *Mascota*.
- **Resultado esperado:** no se abren las pantallas del veterinario; *Mascota* solo muestra las mascotas de ese cliente.
- **Verificación previa:** 2026-11-25 · Correcto · al escribir `/facturacion` vuelve a *Reservar cita*; la API de clientes responde 403 a la clienta. La prueba automática confirma que no puede reservar para la mascota de otro cliente y que el veterinario no entra al portal.
- **Revisión del responsable:** Aprobado (2026-11-25)
- **Observaciones (funcionamiento y visual):**
