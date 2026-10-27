package com.hospivet.factura.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FacturaResponse(
        Long idFactura,
        String numero,
        Long idCliente,
        String cliente,
        String correoCliente,
        LocalDateTime fechaHora,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal total,
        String estado,
        List<DetalleResponse> detalle
) {
    /** Número visible de 6 dígitos, como en las interfaces (000124). */
    public static String numero(Long idFactura) {
        return "%06d".formatted(idFactura);
    }
}
