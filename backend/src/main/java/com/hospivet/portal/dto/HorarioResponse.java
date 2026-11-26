package com.hospivet.portal.dto;

/** Botón de HORARIOS DISPONIBLES: "09:00" o, si no está libre, "09:00 ocupado" (R10.1.1). */
public record HorarioResponse(String hora, boolean disponible) {
}
