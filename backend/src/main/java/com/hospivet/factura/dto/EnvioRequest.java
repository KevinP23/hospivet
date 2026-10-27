package com.hospivet.factura.dto;

import jakarta.validation.constraints.NotBlank;

/** R7.5.1: medio de envío; el único medio documentado es el correo electrónico. */
public record EnvioRequest(@NotBlank(message = "Datos inválidos") String medio) {
}
