package com.hospivet.portal.dto;

/** Opción del campo MASCOTA: solo las mascotas activas del cliente de la sesión. */
public record MascotaPortalResponse(Long idMascota, String nombre) {
}
