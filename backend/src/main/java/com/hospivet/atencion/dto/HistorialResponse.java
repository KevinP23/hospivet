package com.hospivet.atencion.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Una fila del historial clínico: una atención con sus diagnósticos, tratamientos y recetas. */
public record HistorialResponse(
        Long idAtencion,
        String codigo,
        LocalDateTime fechaHora,
        String atencion,
        List<String> diagnosticos,
        List<String> tratamientos,
        List<String> recetas
) {
}
