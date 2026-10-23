package com.hospivet.atencion.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;

@TableName("tratamiento")
public class Tratamiento {

    @TableId(value = "id_tratamiento", type = IdType.AUTO)
    private Long idTratamiento;
    private Long idDiagnostico;
    private String indicaciones;
    private String duracion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Long getIdTratamiento() { return idTratamiento; }
    public void setIdTratamiento(Long idTratamiento) { this.idTratamiento = idTratamiento; }
    public Long getIdDiagnostico() { return idDiagnostico; }
    public void setIdDiagnostico(Long idDiagnostico) { this.idDiagnostico = idDiagnostico; }
    public String getIndicaciones() { return indicaciones; }
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }
    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
}
