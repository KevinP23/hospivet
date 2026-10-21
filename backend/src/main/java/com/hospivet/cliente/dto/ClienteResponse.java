package com.hospivet.cliente.dto;

import com.hospivet.cliente.model.Cliente;

public record ClienteResponse(
        Long idCliente,
        String ci,
        String nombre,
        String apellido,
        String telefono,
        String correo,
        String direccion,
        Boolean activo
) {
    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
                cliente.getIdCliente(), cliente.getCi(), cliente.getNombre(), cliente.getApellido(),
                cliente.getTelefono(), cliente.getCorreo(), cliente.getDireccion(), cliente.getActivo());
    }
}

