package com.hospivet.atencion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** La frecuencia es obligatoria por el diccionario 4.7.10 y usa el mensaje de R8.4.3 (N-03). */
public record RecetaCreateRequest(
        @NotBlank(message = "Complete medicamento y dosis")
        @Size(max = 120, message = "Complete medicamento y dosis")
        String medicamento,
        @NotBlank(message = "Complete medicamento y dosis")
        @Size(max = 60, message = "Complete medicamento y dosis")
        String dosis,
        @NotBlank(message = "Complete medicamento y dosis")
        @Size(max = 60, message = "Complete medicamento y dosis")
        String frecuencia,
        @Size(max = 60, message = "Complete medicamento y dosis")
        String duracion,
        @Size(max = 255, message = "Complete medicamento y dosis")
        String indicaciones
) {
}
