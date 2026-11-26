package com.hospivet.portal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * "Horarios configurados" de la precondición de 3.1.30 (N-03 A, D-21): lunes a sábado, de 09:00 a 12:00 y
 * de 15:00 a 18:00, cada 30 minutos. Se puede cambiar con las propiedades hospivet.portal.*.
 */
@Component
public class HorarioAtencion {

    private final Set<DayOfWeek> dias;
    private final List<LocalTime[]> bloques;
    private final int intervaloMin;

    public HorarioAtencion(
            @Value("${hospivet.portal.dias-atencion:MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY}") String dias,
            @Value("${hospivet.portal.bloques:09:00-12:00,15:00-18:00}") String bloques,
            @Value("${hospivet.portal.intervalo-min:30}") int intervaloMin) {
        this.dias = Arrays.stream(dias.split(",")).map(String::trim).map(DayOfWeek::valueOf)
                .collect(Collectors.toUnmodifiableSet());
        this.bloques = Arrays.stream(bloques.split(",")).map(String::trim)
                .map(bloque -> bloque.split("-"))
                .map(partes -> new LocalTime[]{LocalTime.parse(partes[0].trim()), LocalTime.parse(partes[1].trim())})
                .toList();
        this.intervaloMin = intervaloMin;
    }

    public boolean atiende(LocalDate fecha) {
        return dias.contains(fecha.getDayOfWeek());
    }

    /** Horas de inicio del día en las que el servicio entra completo dentro de un bloque de atención. */
    public List<LocalTime> horasDeInicio(LocalDate fecha, int duracionMin) {
        List<LocalTime> horas = new ArrayList<>();
        if (!atiende(fecha)) {
            return horas;
        }
        for (LocalTime[] bloque : bloques) {
            for (LocalTime hora = bloque[0];
                 !hora.plusMinutes(duracionMin).isAfter(bloque[1]) && hora.isBefore(bloque[1]);
                 hora = hora.plusMinutes(intervaloMin)) {
                horas.add(hora);
            }
        }
        return horas;
    }
}
