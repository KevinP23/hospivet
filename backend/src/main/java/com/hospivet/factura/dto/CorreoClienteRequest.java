package com.hospivet.factura.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** N-10: Actualizar correo guarda el correo directamente en el registro del cliente. */
public record CorreoClienteRequest(
        @NotBlank(message = "El cliente no tiene correo registrado")
        @Email(message = "Correo inválido")
        @Size(max = 100, message = "Correo inválido") String correo
) {
}
