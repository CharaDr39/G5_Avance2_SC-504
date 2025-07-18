package com.frenchies.g5_avance2_sc504.controller;

import java.util.List;
import java.util.Map;

import com.frenchies.g5_avance2_sc504.dto.CreateRoleRequest;
import com.frenchies.g5_avance2_sc504.dto.UpdateRoleRequest;
import com.frenchies.g5_avance2_sc504.service.RoleService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody CreateRoleRequest req) {
        long newId = roleService.createRole(req.getNombre(), req.getDescripcion());
        return ResponseEntity.ok(Map.of("rol_id", newId));
    }

    // READ (LIST)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() {
        return ResponseEntity.ok(roleService.listRoles());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
        @PathVariable long id,
        @RequestBody UpdateRoleRequest req
    ) {
        roleService.updateRole(id, req.getNombre(), req.getDescripcion());
        return ResponseEntity.noContent().build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
