package com.hospivet.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteCreateRequest(
        @NotBlank(message = "Complete los campos obligatorios")
        @Pattern(regexp = "\\d+", message = "CI inválido")
        @Size(max = 15, message = "El CI no puede superar 15 caracteres")
        String ci,

        @NotBlank(message = "Complete los campos obligatorios")
        @Size(max = 60, message = "El nombre no puede superar 60 caracteres")
        String nombre,

        @NotBlank(message = "Complete los campos obligatorios")
        @Size(max = 60, message = "El apellido no puede superar 60 caracteres")
        String apellido,

        @NotBlank(message = "Complete los campos obligatorios")
        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        String telefono,

        @Email(message = "Correo inválido")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres")
        String correo,

        @Size(max = 150, message = "La dirección no puede superar 150 caracteres")
        String direccion
) {
}

