// src/main/java/com/frenchies/g5_avance2_sc504/controller/DetalleFacturaController.java
package com.frenchies.g5_avance2_sc504.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.frenchies.g5_avance2_sc504.service.DetalleFacturaService;

@RestController
@RequestMapping("/facturas/{facturaId}/detalle")
public class DetalleFacturaController {

    private final DetalleFacturaService svc;

    public DetalleFacturaController(DetalleFacturaService svc) {
        this.svc = svc;
    }

    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> list(@PathVariable long facturaId) {
        return ResponseEntity.ok(svc.list(facturaId));
    }

    @PostMapping
    public ResponseEntity<Map<String,Object>> add(
            @PathVariable long facturaId,
            @RequestBody Map<String,Object> b) {
        long productoId = ((Number)b.get("productoId")).longValue();
        long cantidad   = ((Number)b.get("cantidad")).longValue();
        double precio   = ((Number)b.get("precio")).doubleValue();
        long id = svc.add(facturaId, productoId, cantidad, precio);
        return ResponseEntity.ok(Map.of("detalle_id", id));
    }

    @PutMapping("/{detalleId}")
    public ResponseEntity<Void> update(
            @PathVariable long facturaId,
            @PathVariable long detalleId,
            @RequestBody Map<String,Object> b) {
        long cantidad = ((Number)b.get("cantidad")).longValue();
        double precio = ((Number)b.get("precio")).doubleValue();
        svc.update(detalleId, cantidad, precio);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{detalleId}")
    public ResponseEntity<Void> delete(
            @PathVariable long facturaId,
            @PathVariable long detalleId) {
        svc.delete(detalleId);
        return ResponseEntity.noContent().build();
    }
}
