package com.frenchies.g5_avance2_sc504.dto;


import java.math.BigDecimal;

public class DetalleLineaDTO {
  private Long productoId;
  private Integer cantidad;
  private BigDecimal precio;

  public Long getProductoId() { return productoId; }
  public void setProductoId(Long productoId) { this.productoId = productoId; }

  public Integer getCantidad() { return cantidad; }
  public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

  public BigDecimal getPrecio() { return precio; }
  public void setPrecio(BigDecimal precio) { this.precio = precio; }
}
