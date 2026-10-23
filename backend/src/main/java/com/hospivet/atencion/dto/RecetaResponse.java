package com.hospivet.atencion.dto;

public record RecetaResponse(
        Long idReceta,
        Long idAtencion,
        String medicamento,
        String dosis,
        String frecuencia,
        String duracion,
        String indicaciones
) {
}
