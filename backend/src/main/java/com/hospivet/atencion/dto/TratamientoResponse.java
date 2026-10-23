package com.hospivet.atencion.dto;

import java.time.LocalDate;

public record TratamientoResponse(
        Long idTratamiento,
        Long idDiagnostico,
        String indicaciones,
        String duracion,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
}
