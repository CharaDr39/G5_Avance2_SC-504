// src/main/java/com/frenchies/g5_avance2_sc504/service/FacturaService.java
package com.frenchies.g5_avance2_sc504.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.frenchies.g5_avance2_sc504.repository.FacturaRepository;

@Service
public class FacturaService {

    private final FacturaRepository repo;
    public FacturaService(FacturaRepository repo) {
        this.repo = repo;
    }

    public long createFactura(long usuarioId, double monto) {
        return repo.insertFactura(usuarioId, monto);
    }

    public List<Map<String,Object>> listFacturas() {
        return repo.listFacturas();
    }

    public void updateFactura(long id, double monto) {
        repo.updateFactura(id, monto);
    }

    public void deleteFactura(long id) {
        repo.deleteFactura(id);
    }
}
