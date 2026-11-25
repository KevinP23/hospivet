package com.hospivet.reporte.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Fila de la interfaz Reporte de ingresos: Fecha, Cliente, Concepto y Monto en Bs. */
public record ReporteIngresoFila(
        LocalDate fecha,
        String cliente,
        String concepto,
        BigDecimal monto
) {
}
