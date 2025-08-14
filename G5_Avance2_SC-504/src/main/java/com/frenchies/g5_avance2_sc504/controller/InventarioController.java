package com.frenchies.g5_avance2_sc504.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.frenchies.g5_avance2_sc504.service.InventarioService;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioService svc;

    public InventarioController(InventarioService svc) {
        this.svc = svc;
    }

    // POST /inventario  { productoId, cantidad, stockMinimo }
    @PostMapping
    public ResponseEntity<Map<String,Object>> crear(@RequestBody Map<String,Object> b) {
        long productoId = ((Number)b.get("productoId")).longValue();
        double cantidad  = ((Number)b.get("cantidad")).doubleValue();       // -> P_CANTIDAD_ACTUAL
        double minimo    = ((Number)b.get("stockMinimo")).doubleValue();
        long id = svc.crear(productoId, cantidad, minimo);
        return ResponseEntity.ok(Map.of("inventario_id", id));
    }

    // GET /inventario
    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> listar() {
        return ResponseEntity.ok(svc.listar());
    }

    // PUT /inventario/{id}  { cantidad, stockMinimo }
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable long id, @RequestBody Map<String,Object> b) {
        double cantidad = ((Number)b.get("cantidad")).doubleValue();        // -> P_CANTIDAD_ACTUAL
        double minimo   = ((Number)b.get("stockMinimo")).doubleValue();
        svc.actualizar(id, cantidad, minimo);
        return ResponseEntity.noContent().build();
    }

    // DELETE /inventario/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable long id) {
        svc.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /inventario/productos  (para combos)
    @GetMapping("/productos")
    public ResponseEntity<List<Map<String,Object>>> productos() {
        return ResponseEntity.ok(svc.productos());
    }
}
