package com.hospivet.factura.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Datos de generar(cliente, items) (SD-19). */
public record FacturaGenerarRequest(
        @NotNull(message = "Datos inválidos") Long idCliente,
        @NotEmpty(message = "Servicio o producto no encontrado") List<@Valid ItemRequest> items,
        Long idPromocion
) {
}
