package com.hospivet.inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record StockUpdateRequest(
        @NotNull(message = "Cantidad inválida")
        @PositiveOrZero(message = "Cantidad inválida")
        Long nuevaCantidad
) {
}
