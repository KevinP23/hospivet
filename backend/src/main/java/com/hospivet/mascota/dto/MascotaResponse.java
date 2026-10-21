package com.hospivet.mascota.dto;

import com.hospivet.mascota.model.Mascota;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public record MascotaResponse(
        Long idMascota,
        Long idCliente,
        String nombre,
        String especie,
        String raza,
        LocalDate fechaNacimiento,
        Integer edad,
        BigDecimal peso,
        String sexo,
        String observaciones,
        Boolean activo
) {
    public static MascotaResponse from(Mascota mascota) {
        Integer edad = mascota.getFechaNacimiento() == null
                ? null
                : Period.between(mascota.getFechaNacimiento(), LocalDate.now()).getYears();
        return new MascotaResponse(
                mascota.getIdMascota(), mascota.getIdCliente(), mascota.getNombre(), mascota.getEspecie(),
                mascota.getRaza(), mascota.getFechaNacimiento(), edad, mascota.getPeso(), mascota.getSexo(),
                mascota.getObservaciones(), mascota.getActivo());
    }
}

