package com.hospivet.inventario.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductoCreateRequest(
        @NotBlank(message = "Complete los campos obligatorios")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotBlank(message = "Complete los campos obligatorios")
        @Size(max = 60, message = "La categoría no puede superar 60 caracteres")
        String categoria,

        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String descripcion,

        @NotNull(message = "Complete los campos obligatorios")
        @DecimalMin(value = "0.0", inclusive = false, message = "Precio o cantidad inválidos")
        BigDecimal precio,

        @NotNull(message = "Complete los campos obligatorios")
        @Positive(message = "Precio o cantidad inválidos")
        Long cantidadInicial,

        @NotNull(message = "Complete los campos obligatorios")
        @PositiveOrZero(message = "Precio o cantidad inválidos")
        Long stockMinimo
) {
}
