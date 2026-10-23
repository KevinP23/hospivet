package com.hospivet.promocion.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Datos de crear(datosPromocion) y editar(idPromocion, datos). Los textos de campo son los de las
 * interfaces de la Iteración 5 ("Campo obligatorio" y la regla del descuento de Editar promoción).
 */
public record PromocionRequest(
        @NotBlank(message = "Campo obligatorio")
        @Size(max = 100, message = "Campo obligatorio")
        String titulo,
        @NotBlank(message = "Campo obligatorio")
        @Size(max = 255, message = "Campo obligatorio")
        String descripcion,
        @NotNull(message = "Campo obligatorio")
        @DecimalMin(value = "0", message = "Descuento numérico entre 0 y 100")
        @DecimalMax(value = "100", message = "Descuento numérico entre 0 y 100")
        @Digits(integer = 3, fraction = 2, message = "Descuento numérico entre 0 y 100")
        BigDecimal porcentajeDescuento,
        @NotNull(message = "Campo obligatorio")
        LocalDate fechaInicio,
        @NotNull(message = "Campo obligatorio")
        LocalDate fechaFin,
        List<Long> idsServicio,
        List<Long> idsProducto
) {
}
