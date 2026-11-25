package com.hospivet.reporte.dto;

/** Fila de la interfaz Reporte de productos: Producto, Categoría, Stock y Estado (DISPONIBLE o AGOTADO). */
public record ReporteProductoFila(
        String producto,
        String categoria,
        Long stock,
        String estado
) {
}
