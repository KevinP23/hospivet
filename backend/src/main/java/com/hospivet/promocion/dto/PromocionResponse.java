package com.hospivet.promocion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PromocionResponse(
        Long idPromocion,
        String codigo,
        String titulo,
        String descripcion,
        BigDecimal porcentajeDescuento,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String estado,
        List<Long> idsServicio,
        List<Long> idsProducto,
        List<String> aplicaA
) {
    public static String codigo(Long idPromocion) {
        return "PROM-%05d".formatted(idPromocion);
    }
}
