package com.hospivet.factura.dto;

import jakarta.validation.constraints.NotBlank;

public record ImpresionRequest(@NotBlank(message = "Error de impresión, verifique la impresora") String impresora) {
}
