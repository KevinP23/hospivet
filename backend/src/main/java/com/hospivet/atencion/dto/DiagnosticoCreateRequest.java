package com.hospivet.atencion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** La atención llega como idAtencion o, si aún no existe, como la cita programada que la origina (N-01). */
public record DiagnosticoCreateRequest(
        Long idAtencion,
        Long idCita,
        @NotBlank(message = "Ingrese un diagnóstico")
        @Size(max = 1000, message = "Ingrese un diagnóstico")
        String descripcion
) {
}
