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

    // ===== crear factura + detalle en una sola transacción =====
    @Transactional
    public Map<String,Object> crearConDetalle(FacturaConDetalleDTO dto) {
        long facturaId = facturaRepo.insertFactura(dto.getUsuarioId(), 0.0);

        if (dto.getItems() != null) {
            for (DetalleLineaDTO it : dto.getItems()) {
                if (it.getProductoId() == null || it.getCantidad() == null || it.getPrecio() == null) {
                    throw new IllegalArgumentException("productoId, cantidad y precio son obligatorios en cada línea");
                }
                long productoId = it.getProductoId().longValue();
                int  cantidad   = Math.toIntExact(it.getCantidad().longValue()); // repo espera int
                double precio   = it.getPrecio().doubleValue();

                detalleRepo.insertLinea(facturaId, productoId, cantidad, precio);
            }
        }

        Map<String,Object> resp = new HashMap<>();
        resp.put("factura_id", facturaId);
        return resp;
    }
}