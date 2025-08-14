/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frenchies.g5_avance2_sc504.dto;

import java.util.List;

public class FacturaConDetalleDTO {
  private Long usuarioId;
  private List<DetalleLineaDTO> items;

  public Long getUsuarioId() { return usuarioId; }
  public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

  public List<DetalleLineaDTO> getItems() { return items; }
  public void setItems(List<DetalleLineaDTO> items) { this.items = items; }
}
