package com.hospivet.factura.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Línea del detalle corregida en Editar factura (N-06). Textos de campo tomados de la interfaz. */
public record LineaRequest(
        @NotNull(message = "Datos inválidos") Long idDetalle,
        @NotBlank(message = "Descripción obligatoria")
        @Size(max = 150, message = "Descripción obligatoria") String descripcion,
        @NotNull(message = "Cantidad mayor que 0")
        @Positive(message = "Cantidad mayor que 0") Long cantidad,
        @NotNull(message = "Precio no negativo")
        @DecimalMin(value = "0", message = "Precio no negativo")
        @Digits(integer = 8, fraction = 2, message = "Precio no negativo") BigDecimal precioUnitario
) {
}
