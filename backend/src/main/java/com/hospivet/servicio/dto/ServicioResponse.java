package com.hospivet.servicio.dto;

import com.hospivet.servicio.model.Servicio;

import java.math.BigDecimal;

public record ServicioResponse(
        Long idServicio,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer duracionMin
) {
    public static ServicioResponse from(Servicio servicio) {
        return new ServicioResponse(servicio.getIdServicio(), servicio.getNombre(), servicio.getDescripcion(),
                servicio.getPrecio(), servicio.getDuracionMin());
    }
}
