package com.hospivet.reporte.dto;

import java.math.BigDecimal;
import java.util.List;

/** R6.2.2: facturas del periodo y total de ingresos del periodo. */
public record ReporteIngresosResponse(
        List<ReporteIngresoFila> filas,
        BigDecimal total
) {
}
