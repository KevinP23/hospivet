package com.hospivet.factura.dto;

import java.math.BigDecimal;

public record DetalleResponse(
        Long idDetalle,
        String tipoItem,
        Long idProducto,
        Long idServicio,
        String descripcion,
        Long cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
