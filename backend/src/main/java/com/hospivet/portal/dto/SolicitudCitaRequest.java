package com.hospivet.portal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/** SD-30 registrar(fecha, horario, mascota), con el servicio elegido en la interfaz. */
public record SolicitudCitaRequest(
        @NotNull(message = "Seleccione la mascota") Long idMascota,
        @NotNull(message = "Seleccione el servicio") Long idServicio,
        @NotNull(message = "Seleccione la fecha") LocalDate fecha,
        @NotNull(message = "Seleccione el horario")
        @Pattern(regexp = "[0-9]{2}:[0-9]{2}", message = "Seleccione el horario") String hora
) {
}
