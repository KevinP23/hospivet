package com.hospivet.cita.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CitaUpdateRequest(
        @NotNull(message = "Seleccione el servicio") Long idServicio,
        @NotNull(message = "Seleccione fecha y hora")
        @Future(message = "Seleccione una fecha y hora futuras") LocalDateTime fechaHora
) {
}
