package com.hospivet.mascota.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MascotaCreateRequest(
        @NotNull(message = "Seleccione el cliente propietario")
        Long idCliente,

        @NotBlank(message = "Complete los campos obligatorios")
        @Size(max = 60, message = "El nombre no puede superar 60 caracteres")
        String nombre,

        @NotBlank(message = "Complete los campos obligatorios")
        @Size(max = 40, message = "La especie no puede superar 40 caracteres")
        String especie,

        @Size(max = 60, message = "La raza no puede superar 60 caracteres")
        String raza,

        @PastOrPresent(message = "Edad o peso inválido")
        LocalDate fechaNacimiento,

        @NotNull(message = "Complete los campos obligatorios")
        @DecimalMin(value = "0.0", inclusive = false, message = "Edad o peso inválido")
        BigDecimal peso,

        @Size(max = 1, message = "El sexo debe ocupar un carácter")
        String sexo,

        @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
        String observaciones
) {
}

