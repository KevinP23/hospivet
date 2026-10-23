package com.hospivet.atencion.dto;

import java.time.LocalDateTime;

public record DiagnosticoResponse(
        Long idDiagnostico,
        String codigo,
        Long idAtencion,
        Long idMascota,
        String mascota,
        String descripcion,
        LocalDateTime fechaRegistro
) {
    public static String codigo(Long idDiagnostico) {
        return "D-%05d".formatted(idDiagnostico);
    }
}
