package com.hospivet.portal.dto;

import java.math.BigDecimal;

/** Tarjeta de Servicios disponibles: nombre, descripción y precio (R10.2.1–R10.2.4). */
public record ServicioPortalResponse(Long idServicio, String nombre, String descripcion, BigDecimal precio) {
}
