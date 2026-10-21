package com.hospivet.inventario.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

@TableName("producto")
public class Producto {

    @TableId(value = "id_producto", type = IdType.AUTO)
    private Long idProducto;
    private String nombre;
    private String categoria;
    private String descripcion;
    private BigDecimal precio;
    private Long stockActual;
    private Long stockMinimo;
    private Boolean activo;

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Long getStockActual() { return stockActual; }
    public void setStockActual(Long stockActual) { this.stockActual = stockActual; }
    public Long getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Long stockMinimo) { this.stockMinimo = stockMinimo; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
