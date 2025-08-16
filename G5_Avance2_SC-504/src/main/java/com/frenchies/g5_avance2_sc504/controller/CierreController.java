// src/main/java/com/frenchies/g5_avance2_sc504/controller/CierreController.java
package com.frenchies.g5_avance2_sc504.controller;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.frenchies.g5_avance2_sc504.service.CierreService;

@RestController
@RequestMapping
public class CierreController {

    private final CierreService svc;
    public CierreController(CierreService svc) { this.svc = svc; }

    // ===== Utils =====
    private LocalDate parseFecha(Object raw) {
        if (raw == null) return LocalDate.now();
        String s = String.valueOf(raw).trim();
        try {
            if (s.length() == 10) return LocalDate.parse(s); // yyyy-MM-dd
            return OffsetDateTime.parse(s).toLocalDate();    // datetime-local, etc.
        } catch (Exception e) {
            return LocalDate.parse(s.substring(0, 10));
        }
    }

    // ===== Cierres =====
    @PostMapping("/cierres")
    public ResponseEntity<Map<String,Object>> create(@RequestBody Map<String,Object> b) {
        String tipo  = String.valueOf(b.getOrDefault("tipo","DIARIO"));
        LocalDate fecha = parseFecha(b.get("fecha"));

        Long id = svc.create(tipo, fecha);

        // total opcional (manual)
        if (b.get("total") != null) {
            double total = ((Number) b.get("total")).doubleValue();
            svc.setTotal(id, total);
            return ResponseEntity.ok(Map.of("cierre_id", id, "total", total));
        }
        return ResponseEntity.ok(Map.of("cierre_id", id));
    }

    @GetMapping("/cierres")
    public ResponseEntity<List<Map<String,Object>>> list() {
        return ResponseEntity.ok(svc.list());
    }

    @PutMapping("/cierres/{id}")
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody Map<String,Object> b) {
        String tipo  = String.valueOf(b.getOrDefault("tipo","DIARIO"));
        LocalDate fecha = parseFecha(b.get("fecha"));
        svc.update(id, tipo, fecha);

        // (opcional) si también mandan total en update, lo persistimos
        if (b.get("total") != null) {
            double total = ((Number) b.get("total")).doubleValue();
            svc.setTotal(id, total);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cierres/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cierres/{id}/total")
    public ResponseEntity<Map<String,Object>> total(@PathVariable long id) {
        return ResponseEntity.ok(Map.of("total", svc.total(id)));
    }

    // ===== Movimientos por Cierre =====
    @GetMapping("/cierres/{id}/movimientos")
    public ResponseEntity<List<Map<String,Object>>> listMov(@PathVariable long id) {
        return ResponseEntity.ok(svc.listMovs(id));
    }

    @PostMapping("/cierres/{id}/movimientos")
    public ResponseEntity<Map<String,Object>> addMov(@PathVariable long id, @RequestBody Map<String,Object> b) {
        String tipo = String.valueOf(b.getOrDefault("tipo","ENTRADA"));
        String desc = (String) b.getOrDefault("descripcion", "");
        double monto = ((Number)b.get("monto")).doubleValue();
        long movId = svc.addMov(id, tipo, desc, monto);
        return ResponseEntity.ok(Map.of("movimiento_id", movId));
    }

    @PutMapping("/movimientos/{movId}")
    public ResponseEntity<Void> updMov(@PathVariable long movId, @RequestBody Map<String,Object> b) {
        String tipo = String.valueOf(b.getOrDefault("tipo","ENTRADA"));
        String desc = (String) b.getOrDefault("descripcion", "");
        double monto = ((Number)b.get("monto")).doubleValue();
        svc.updMov(movId, tipo, desc, monto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/movimientos/{movId}")
    public ResponseEntity<Void> delMov(@PathVariable long movId) {
        svc.delMov(movId);
        return ResponseEntity.noContent().build();
    }
}