# Módulos de HOSPIVET

Estado de cada módulo según `docs/METODOLOGIA.md`. Los módulos R01 a R05 se construyeron antes de adoptar la metodología: su plan es retroactivo y queda en revisión hasta la prueba manual del responsable.

| Módulo | Requisitos | Secuencias | Estado | Plan |
|---|---|---|---|---|
| R01 Autenticación | R1.1 implementado · R1.2 pendiente | SD-01, SD-02 | Implementado (retroactivo) · En revisión | [plan](R01-autenticacion/plan.md) |
| R02 Clientes | R2.1 | SD-03 | Implementado (retroactivo) · En revisión | [plan](R02-clientes/plan.md) |
| R03 Mascotas | R3.1 | SD-04 | Implementado (retroactivo) · En revisión | [plan](R03-mascotas/plan.md) |
| R04 Inventario | R4.1–R4.4 | SD-05 a SD-08 | Implementado (retroactivo) · En revisión | [plan](R04-inventario/plan.md) |
| R05 Citas | R5.1–R5.4 | SD-09 a SD-12 | Implementado (retroactivo) · En revisión | [plan](R05-citas/plan.md) |
| COR-01 Correcciones de la auditoría | AUD-01 a AUD-15 | — | Cerrado (2026-10-22) | [plan](COR-01-correcciones-auditoria/plan.md) |
| R08 Atención médica | R8.1–R8.4 | SD-22 a SD-25 | Cerrado (2026-10-23) | [plan](R08-atencion-medica/plan.md) |
| R09 Promociones | R9.1–R9.4 | SD-26 a SD-29 | Cerrado (2026-10-23) | [plan](R09-promociones/plan.md) |
| R07 Facturación | R7.1–R7.5 | SD-17 a SD-21 | Cerrado (2026-10-27) | [plan](R07-facturacion/plan.md) |
| R06 Reportes | R6.1–R6.4 | SD-13 a SD-16 | Sin plan | — |
| R01 Recuperar contraseña | R1.2 | SD-02 | Sin plan | — |
| R10 Consulta del cliente | R10.1–R10.3 | SD-30 a SD-32 | Sin plan | — |

## Orden propuesto para los módulos nuevos

1. **R08 Atención médica**: base clínica; marca la cita como `ATENDIDA` y alimenta el historial.
2. **R09 Promociones**: la factura aplica el descuento de la promoción vigente.
3. **R07 Facturación**: usa atenciones, servicios, productos y promociones.
4. **R06 Reportes**: los ingresos y servicios se calculan desde facturas y citas.
5. **R1.2 Recuperar contraseña**: requiere definir el envío de correo.
6. **R10 Consulta del cliente**: requiere habilitar el acceso con rol `CLIENTE`.

Auditoría del avance inicial: [`docs/auditoria/2026-10-21-auditoria-avance-inicial.md`](../auditoria/2026-10-21-auditoria-avance-inicial.md).
