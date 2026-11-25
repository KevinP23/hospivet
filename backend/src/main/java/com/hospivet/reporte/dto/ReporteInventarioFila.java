package com.hospivet.reporte.dto;

import java.math.BigDecimal;

/** Fila de la interfaz Reporte de inventario: Producto, Categoría, Stock actual, Stock mínimo y Valor en Bs. */
public record ReporteInventarioFila(
        String producto,
        String categoria,
        Long stockActual,
        Long stockMinimo,
        BigDecimal valor
) {
}
