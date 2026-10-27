package com.hospivet.factura.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Datos de editar(idFactura, datos, items) (SD-18). */
public record FacturaEditarRequest(
        @NotNull(message = "Cliente obligatorio") Long idCliente,
        @NotNull(message = "Fecha válida") LocalDate fecha,
        @NotNull(message = "Descuento entre 0 y el subtotal")
        @DecimalMin(value = "0", message = "Descuento entre 0 y el subtotal") BigDecimal descuento,
        @NotEmpty(message = "Datos inválidos") List<@Valid LineaRequest> lineas
) {
}
