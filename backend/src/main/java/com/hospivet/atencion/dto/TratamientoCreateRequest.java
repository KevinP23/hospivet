package com.hospivet.atencion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TratamientoCreateRequest(
        @NotBlank(message = "Ingrese un tratamiento")
        @Size(max = 1000, message = "Ingrese un tratamiento")
        String indicaciones,
        @Size(max = 60, message = "Ingrese un tratamiento")
        String duracion,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
}
