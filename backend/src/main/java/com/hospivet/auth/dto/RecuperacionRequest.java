package com.hospivet.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** R1.2.1: usuario o correo registrado (campo "Usuario o correo registrado" de la interfaz). */
public record RecuperacionRequest(
        @NotBlank(message = "Usuario no registrado")
        @Size(max = 100, message = "Usuario no registrado")
        String identidad
) {
}
