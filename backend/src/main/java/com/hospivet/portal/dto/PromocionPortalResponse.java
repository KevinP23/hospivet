package com.hospivet.portal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Tarjeta de Promociones vigentes y su "Detalle de la promoción" (R10.3.1–R10.3.4, N-08). */
public record PromocionPortalResponse(
        Long idPromocion,
        String titulo,
        String descripcion,
        BigDecimal porcentajeDescuento,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String detalle
) {
}
