package com.frenchies.g5_avance2_sc504.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frenchies.g5_avance2_sc504.dto.CreateRoleRequest;
import com.frenchies.g5_avance2_sc504.service.RoleService;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody CreateRoleRequest req) {
        long newId = roleService.createRole(req.getNombre(), req.getDescripcion());
        return ResponseEntity.ok(Map.of("rol_id", newId));
    }
}
