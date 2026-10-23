package com.hospivet.atencion.dto;

import java.time.LocalDateTime;

public record CitaProgramadaResponse(
        Long idCita,
        String codigo,
        LocalDateTime fechaHora,
        String motivo
) {
}
