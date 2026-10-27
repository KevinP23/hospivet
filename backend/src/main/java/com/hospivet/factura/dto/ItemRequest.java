package com.hospivet.factura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Ítem agregado al detalle en Generar factura: servicio o producto del catálogo y su cantidad (N-02). */
public record ItemRequest(
        @NotBlank(message = "Servicio o producto no encontrado") String tipo,
        @NotNull(message = "Servicio o producto no encontrado") Long id,
        @NotNull(message = "Servicio o producto no encontrado")
        @Positive(message = "Servicio o producto no encontrado") Long cantidad
) {
}
