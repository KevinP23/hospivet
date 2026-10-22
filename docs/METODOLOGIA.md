# Metodología de trabajo — HOSPIVET

Desarrollo guiado por especificaciones (SDD): ningún cambio de código empieza sin un plan aprobado, y ningún módulo se cierra sin la revisión humana del responsable del proyecto.

## Fuente de verdad

1. `Iteracion5_Hospivet_integrada.docx`: requisitos R1 a R10, casos de uso, interfaces y secuencias SD-01 a SD-32.
2. `Iteracion4_Hospivet_modelos_corregidos.docx`: contraste del modelo de clases y de datos.
3. `docs/modulos/<módulo>/plan.md`: especificación aprobada del módulo. Si el plan se aparta de los documentos Word, la diferencia se justifica en el propio plan y en `docs/trazabilidad/decisiones.md`.

Los documentos Word no se modifican.

## Ciclo de cada módulo

| Fase | Quién | Resultado | Puerta |
|---|---|---|---|
| 1. Plan | Desarrollo | `plan.md` en estado **Borrador** | El responsable aprueba o pide cambios |
| 2. Codificación | Desarrollo | Código según el plan **Aprobado** | — |
| 3. Verificación técnica | Desarrollo | Casos de `pruebas.md` ejecutados en la aplicación y anotados como verificación previa | — |
| 4. Revisión humana | Responsable | Casos revisados a mano: funcionamiento y aspecto visual | **Aprueba**, **observa** o **rechaza** |
| 5. Corrección | Desarrollo | Cambios pedidos; si alteran el alcance, se vuelve a la fase 1 | Se repite la fase 4 |
| 6. Cierre | Desarrollo y responsable | Commits en *Commits pendientes* del manual Git y evidencia de estudio para la defensa | El responsable ejecuta los commits |

Reglas:

- **Sin plan aprobado no hay código.** La aprobación solo la da el responsable del proyecto y se registra en `revisiones.md` del módulo.
- **Las pruebas de aceptación son manuales.** Se describen como casos paso a paso en `pruebas.md`. La verificación previa del desarrollo no sustituye la decisión final del responsable.
- Cada observación del responsable se registra en `revisiones.md` con su respuesta.
- Un cambio que modifica requisitos, tablas o interfaces exige actualizar el plan antes de codificar.

## Estados del plan

`Borrador` → `Aprobado` → `En codificación` → `En verificación` → `En revisión` → `Cerrado`

- `Observado`: la revisión pidió cambios; vuelve a `En codificación` (o a `Borrador` si cambia el alcance).
- `Implementado (retroactivo)`: módulo construido antes de adoptar esta metodología; su plan describe lo existente y queda `En revisión` hasta la revisión humana.

## Estructura

```
docs/
├── METODOLOGIA.md          este documento
├── plantillas/             plan.md, pruebas.md, revisiones.md
├── modulos/
│   ├── README.md           índice y estado de cada módulo
│   └── RXX-nombre/         plan.md, pruebas.md, revisiones.md
├── auditoria/              revisiones técnicas del código
└── trazabilidad/           decisiones y correspondencia con las iteraciones
```

## Casos de prueba

Cada caso (`CP-RXX-NN`) indica requisito, precondición, pasos, resultado esperado y dos resultados separados:

- **Verificación previa**: ejecutada por desarrollo en la aplicación en marcha (fecha y resultado).
- **Revisión del responsable**: `Aprobado`, `Observado` o `Rechazado`, con observaciones de funcionamiento y visuales.

Las pruebas automatizadas existentes en `backend/src/test` se conservan como apoyo técnico de regresión; no reemplazan la revisión humana ni cuentan como aceptación.

## Convenciones

- Mensajes visibles exactamente como en la Iteración 5 (por ejemplo, `Horario no disponible, seleccione otro`).
- Cada pantalla muestra sus referencias de requisitos.
- Cada migración de base de datos es nueva y versionada (Flyway `V<n>__descripcion.sql`); nunca se editan migraciones aplicadas.
- Identificadores de módulo: R01 Autenticación, R02 Clientes, R03 Mascotas, R04 Inventario, R05 Citas, R06 Reportes, R07 Facturación, R08 Atención médica, R09 Promociones, R10 Consulta del cliente.
