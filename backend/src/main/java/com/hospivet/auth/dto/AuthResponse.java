package com.hospivet.auth.dto;

public record AuthResponse(Long idUsuario, String usuario, String rol, String nombre, String apellido) {
}
