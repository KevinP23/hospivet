package com.hospivet.inventario.dto;

import com.hospivet.inventario.model.Producto;

import java.math.BigDecimal;

public record ProductoResponse(
        Long idProducto,
        String codigo,
        String nombre,
        String categoria,
        String descripcion,
        BigDecimal precio,
        Long stockActual,
        Long stockMinimo,
        String estado
) {
    public static ProductoResponse from(Producto producto) {
        return new ProductoResponse(
                producto.getIdProducto(),
                codigo(producto.getIdProducto()),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStockActual(),
                producto.getStockMinimo(),
                estado(producto)
        );
    }

    public static String codigo(Long idProducto) {
        return "PRD-%06d".formatted(idProducto);
    }

    private static String estado(Producto producto) {
        if (producto.getStockActual() == 0) return "AGOTADO";
        if (producto.getStockActual() <= producto.getStockMinimo()) return "STOCK_BAJO";
        return "DISPONIBLE";
    }
}
