
package com.frenchies.g5_avance2_sc504.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.frenchies.g5_avance2_sc504.service.RoleService;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService svc;
    public RoleController(RoleService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, String> b) {
        long id = svc.createRole(b.get("nombre"), b.get("descripcion"));
        return ResponseEntity.ok(Map.of("rol_id", id));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() {
        return ResponseEntity.ok(svc.getAllRoles());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
        @PathVariable long id,
        @RequestBody Map<String, String> b
    ) {
        svc.updateRole(id, b.get("nombre"), b.get("descripcion"));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        svc.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
