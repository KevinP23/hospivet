package com.hospivet.cita.dto;

import java.time.LocalDateTime;

public record CitaResponse(
        Long idCita,
        String codigo,
        Long idCliente,
        String cliente,
        Long idMascota,
        String mascota,
        Long idVeterinario,
        String veterinario,
        Long idServicio,
        String servicio,
        LocalDateTime fechaHora,
        String estado,
        String motivo,
        String observaciones
) {
    public static String codigo(Long idCita) {
        return "CITA-%05d".formatted(idCita);
    }
}
