package com.frenchies.g5_avance2_sc504.controller;

import com.frenchies.g5_avance2_sc504.repository.DetalleFacturaRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/detalle-factura")
public class DetalleFacturaController {

    private final DetalleFacturaRepository repo;

    public DetalleFacturaController(DetalleFacturaRepository repo) {
        this.repo = repo;
    }

    // Listar líneas de una factura
    @GetMapping("/{facturaId}")
    public ResponseEntity<List<Map<String,Object>>> list(@PathVariable long facturaId) {
        return ResponseEntity.ok(repo.listByFactura(facturaId));
    }

    // Agregar línea (form-urlencoded: productoId, cantidad, precio)
    @PostMapping(value = "/{facturaId}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String,Object>> add(
            @PathVariable long facturaId,
            @RequestParam("productoId") long productoId,
            @RequestParam("cantidad") long cantidad,
            @RequestParam("precio") Double precioUnitario
    ) {
        int cant = Math.toIntExact(cantidad); // el repo espera int
        long detId = repo.insertLinea(facturaId, productoId, cant, precioUnitario);
        return ResponseEntity.ok(Map.of("detalle_id", detId));
    }

    // Actualizar línea (JSON: {cantidad, precio})
    @PutMapping("/linea/{id}")
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody Map<String,Object> body) {
        Number c = (Number) body.get("cantidad");
        Number p = (Number) body.get("precio");
        int cant = Math.toIntExact(c.longValue());
        double precio = p.doubleValue();
        repo.updateDetalle(id, cant, precio); // nombre correcto en el repo
        return ResponseEntity.noContent().build();
    }

    // Eliminar línea
    @DeleteMapping("/linea/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        repo.deleteDetalle(id); // nombre correcto en el repo
        return ResponseEntity.noContent().build();
    }
}