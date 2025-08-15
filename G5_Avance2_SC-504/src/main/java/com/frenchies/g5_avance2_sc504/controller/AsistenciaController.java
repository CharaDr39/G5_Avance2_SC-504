package com.frenchies.g5_avance2_sc504.controller;

import com.frenchies.g5_avance2_sc504.service.AsistenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
// Soporta /asistencia (contrato) y /asistencias (fallback del front)
@RequestMapping({"/asistencia","/asistencias"})
public class AsistenciaController {

    private final AsistenciaService svc;

    public AsistenciaController(AsistenciaService svc) {
        this.svc = svc;
    }

    // ====== LISTAR TODAS ======
    // GET /asistencia  y  GET /asistencias
    @GetMapping
    public ResponseEntity<?> listarTodas() {
        List<Map<String,Object>> rows = svc.listAll();
        if (rows == null || rows.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(rows);
    }

    @PostMapping
    public ResponseEntity<Map<String,Object>> crear(@RequestBody Map<String,Object> b) {
        long usuarioId = ((Number) b.get("usuarioId")).longValue();
        String fecha = (String) b.get("fecha"); // "yyyy-MM-dd"
        String hEntrada  = (String) b.getOrDefault("horaEntrada",  "");
        String hSalida   = (String) b.getOrDefault("horaSalida",   "");
        String hAlmuerzo = (String) b.getOrDefault("horaAlmuerzo", "");
        long id = svc.crear(usuarioId, fecha, hEntrada, hSalida, hAlmuerzo);
        return ResponseEntity.ok(Map.of("asistencia_id", id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable long id, @RequestBody Map<String,Object> b) {
        String fecha     = (String) b.getOrDefault("fecha", "");
        String hSalida   = (String) b.getOrDefault("horaSalida", "");
        String hAlmuerzo = (String) b.getOrDefault("horaAlmuerzo", "");
        svc.actualizar(id, fecha, hSalida, hAlmuerzo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable long id) {
        svc.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /asistencia/hoy  y  /asistencias/hoy (se mantiene)
    @GetMapping("/hoy")
    public ResponseEntity<List<Map<String,Object>>> hoy() {
        return ResponseEntity.ok(svc.hoy());
    }
}
