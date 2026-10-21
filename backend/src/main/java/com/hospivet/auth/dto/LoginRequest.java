package com.hospivet.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Ingrese el usuario")
        @Size(max = 50, message = "El usuario no puede superar 50 caracteres")
        String usuario,

        @NotBlank(message = "Ingrese la contraseña")
        String contrasena
) {
}

