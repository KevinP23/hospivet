package com.hospivet.cita.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CitaCreateRequest(
        @NotNull(message = "Seleccione el cliente") Long idCliente,
        @NotNull(message = "Seleccione la mascota") Long idMascota,
        @NotNull(message = "Seleccione el servicio solicitado") Long idServicio,
        @NotNull(message = "Seleccione fecha y hora")
        @Future(message = "Seleccione una fecha y hora futuras") LocalDateTime fechaHora,
        @NotBlank(message = "Complete el motivo")
        @Size(max = 255, message = "El motivo no puede superar 255 caracteres") String motivo,
        @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres") String observaciones
) {
}
