package com.hospivet.promocion.model;

import com.baomidou.mybatisplus.annotation.TableName;

/** Tabla asociativa N:M entre promoción y producto (diccionario 4.7.16). */
@TableName("promocion_producto")
public class PromocionProducto {

    private Long idPromocion;
    private Long idProducto;

    public PromocionProducto() {
    }

    public PromocionProducto(Long idPromocion, Long idProducto) {
        this.idPromocion = idPromocion;
        this.idProducto = idProducto;
    }

    public Long getIdPromocion() { return idPromocion; }
    public void setIdPromocion(Long idPromocion) { this.idPromocion = idPromocion; }
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
}
