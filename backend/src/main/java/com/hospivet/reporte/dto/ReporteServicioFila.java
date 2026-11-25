package com.hospivet.reporte.dto;

import java.math.BigDecimal;

/** Fila de la interfaz Reporte de servicios: Servicio, Cantidad realizada e Ingreso en Bs. */
public record ReporteServicioFila(
        String servicio,
        Long cantidad,
        BigDecimal ingreso
) {
}
