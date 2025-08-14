package com.frenchies.g5_avance2_sc504.controller;

import java.util.List;
import java.util.Map;
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

    @PostMapping
    public ResponseEntity<Map<String,Object>> crear(@RequestBody Map<String,Object> b) {
        String nombre = String.valueOf(b.get("nombre"));
        Number precio = (Number) b.get("precio");
        long id = svc.crear(nombre, precio);
        return ResponseEntity.ok(Map.of("producto_id", id));
    }

    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> listar() {
        return ResponseEntity.ok(svc.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable long id, @RequestBody Map<String,Object> b) {
        String nombre = String.valueOf(b.get("nombre"));
        Number precio = (Number) b.get("precio");
        svc.actualizar(id, nombre, precio);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable long id) {
        svc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
