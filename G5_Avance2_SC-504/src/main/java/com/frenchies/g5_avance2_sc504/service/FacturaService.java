// src/main/java/com/frenchies/g5_avance2_sc504/service/FacturaService.java
package com.frenchies.g5_avance2_sc504.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frenchies.g5_avance2_sc504.dto.FacturaConDetalleDTO;
import com.frenchies.g5_avance2_sc504.dto.DetalleLineaDTO;
import com.frenchies.g5_avance2_sc504.repository.DetalleFacturaRepository;
import com.frenchies.g5_avance2_sc504.repository.FacturaRepository;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepo;
    private final DetalleFacturaRepository detalleRepo;

    public FacturaService(FacturaRepository facturaRepo, DetalleFacturaRepository detalleRepo) {
        this.facturaRepo = facturaRepo;
        this.detalleRepo = detalleRepo;
    }

    // ===== CRUD simple =====
    public long createFactura(long usuarioId, double monto) {
        return facturaRepo.insertFactura(usuarioId, monto);
    }

    public List<Map<String,Object>> listFacturas() {
        return facturaRepo.listFacturas();
    }

    public void updateFactura(long id, double monto) {
        facturaRepo.updateFactura(id, monto);
    }

    public void deleteFactura(long id) {
        facturaRepo.deleteFactura(id);
    }

    // ===== NUEVO: crear factura + detalle en una sola transacción =====
    @Transactional
    public Map<String,Object> crearConDetalle(FacturaConDetalleDTO dto) {
        // 1) crear factura con monto 0 (el trigger ya recalcula el total)
        long facturaId = facturaRepo.insertFactura(dto.getUsuarioId(), 0.0);

        // 2) insertar líneas (validando campos y convirtiendo tipos)
        if (dto.getItems() != null) {
            for (DetalleLineaDTO it : dto.getItems()) {
                if (it.getProductoId() == null || it.getCantidad() == null || it.getPrecio() == null) {
                    throw new IllegalArgumentException("productoId, cantidad y precio son obligatorios en cada línea");
                }
                long productoId = it.getProductoId().longValue();
                long cantidad   = it.getCantidad().longValue();
                double precio   = it.getPrecio().doubleValue();

                // OJO: el método correcto del repo es insertLinea(...)
                detalleRepo.insertLinea(
                    facturaId,
                    productoId,
                    cantidad,
                    precio
                );
            }
        }

        // 3) respuesta
        Map<String,Object> resp = new HashMap<>();
        resp.put("factura_id", facturaId);
        return resp;
    }
}
