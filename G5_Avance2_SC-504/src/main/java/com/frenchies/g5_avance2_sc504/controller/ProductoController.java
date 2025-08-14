package com.frenchies.g5_avance2_sc504.controller;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.frenchies.g5_avance2_sc504.service.ProductoService;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService svc;

    public ProductoController(ProductoService svc) {
        this.svc = svc;
    }

    private static Number num(Object o) { return (o instanceof Number) ? (Number) o : null; }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String,Object> b) {
        try {
            String nombre     = String.valueOf(b.get("nombre"));
            Number precio     = num(b.get("precio"));
            Number stockAct   = num(b.get("stockActual"));   // opcional, default 0
            Number ptoReorden = num(b.get("puntoReorden"));  // opcional, default 0
            long id = svc.crear(nombre, precio, stockAct, ptoReorden);
            return ResponseEntity.ok(Map.of("producto_id", id));
        } catch (DataAccessException ex) {
            // Mensaje de Oracle al cliente (útil en desarrollo)
            return ResponseEntity.badRequest().body(Map.of(
                "error", "No se pudo crear el producto",
                "detalle", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> listar() {
        return ResponseEntity.ok(svc.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable long id, @RequestBody Map<String,Object> b) {
        try {
            String nombre     = String.valueOf(b.get("nombre"));
            Number precio     = num(b.get("precio"));
            Number stockAct   = num(b.get("stockActual"));   // opcional
            Number ptoReorden = num(b.get("puntoReorden"));  // opcional
            svc.actualizar(id, nombre, precio, stockAct, ptoReorden);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "No se pudo actualizar el producto",
                "detalle", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable long id) {
        try {
            svc.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            // Caso típico: ORA-20024 Producto con movimientos -> 409 Conflict
            String detalle = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
            return ResponseEntity.status(409).body(Map.of(
                "error", "No se puede eliminar el producto (tiene movimientos o referencias).",
                "detalle", detalle
            ));
        }
    }
}
