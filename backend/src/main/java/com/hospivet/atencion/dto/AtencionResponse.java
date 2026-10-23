package com.hospivet.atencion.dto;

import java.time.LocalDateTime;

public record AtencionResponse(
        Long idAtencion,
        String codigo,
        Long idMascota,
        String mascota,
        LocalDateTime fechaHora
) {
    public static String codigo(Long idAtencion) {
        return "AT-%05d".formatted(idAtencion);
    }
}
