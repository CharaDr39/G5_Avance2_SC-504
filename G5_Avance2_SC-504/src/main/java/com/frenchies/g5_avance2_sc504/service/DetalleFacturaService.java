package com.frenchies.g5_avance2_sc504.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.frenchies.g5_avance2_sc504.repository.DetalleFacturaRepository;

@Service
public class DetalleFacturaService {

    private final DetalleFacturaRepository repo;

    public DetalleFacturaService(DetalleFacturaRepository repo) {
        this.repo = repo;
    }

    public List<Map<String,Object>> list(long facturaId) {
        return repo.listByFactura(facturaId);
    }

    public long add(long facturaId, long productoId, int cantidad, Double precio) {
        return repo.insertLinea(facturaId, productoId, cantidad, precio);
    }

    public void update(long detalleId, int cantidad, double precio) {
        repo.updateDetalle(detalleId, cantidad, precio);
    }

    public void delete(long detalleId) {
        repo.deleteDetalle(detalleId);
    }
}