// src/main/java/com/frenchies/g5_avance2_sc504/controller/FacturaController.java
package com.frenchies.g5_avance2_sc504.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.frenchies.g5_avance2_sc504.dto.FacturaConDetalleDTO;
import com.frenchies.g5_avance2_sc504.service.FacturaService;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaService svc;
    public FacturaController(FacturaService svc) { this.svc = svc; }

    // === Crear SOLO factura (lo que ya tenías) ===
    @PostMapping
    public ResponseEntity<Map<String,Object>> create(@RequestBody Map<String,Object> b) {
        long uid  = ((Number)b.get("usuarioId")).longValue();
        double m  = ((Number)b.get("montoTotal")).doubleValue();
        long id = svc.createFactura(uid, m);
        return ResponseEntity.ok(Map.of("factura_id", id));
    }

    // === NUEVO: crear factura + detalle en un solo paso ===
    @PostMapping("/con-detalle")
    public ResponseEntity<Map<String,Object>> crearConDetalle(@RequestBody FacturaConDetalleDTO dto) {
        return ResponseEntity.ok(svc.crearConDetalle(dto));
    }

    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> list() {
        return ResponseEntity.ok(svc.listFacturas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
        @PathVariable long id,
        @RequestBody Map<String,Object> b
    ) {
        double m = ((Number)b.get("montoTotal")).doubleValue();
        svc.updateFactura(id, m);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        svc.deleteFactura(id);
        return ResponseEntity.noContent().build();
    }
}
