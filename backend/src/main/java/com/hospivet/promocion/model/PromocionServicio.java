package com.hospivet.promocion.model;

import com.baomidou.mybatisplus.annotation.TableName;

/** Tabla asociativa N:M entre promoción y servicio (diccionario 4.7.17). */
@TableName("promocion_servicio")
public class PromocionServicio {

    private Long idPromocion;
    private Long idServicio;

    public PromocionServicio() {
    }

    public PromocionServicio(Long idPromocion, Long idServicio) {
        this.idPromocion = idPromocion;
        this.idServicio = idServicio;
    }

    public Long getIdPromocion() { return idPromocion; }
    public void setIdPromocion(Long idPromocion) { this.idPromocion = idPromocion; }
    public Long getIdServicio() { return idServicio; }
    public void setIdServicio(Long idServicio) { this.idServicio = idServicio; }
}
