// src/main/java/com/frenchies/g5_avance2_sc504/controller/UserController.java
package com.frenchies.g5_avance2_sc504.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.frenchies.g5_avance2_sc504.service.UserService;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    private final UserService svc;
    public UserController(UserService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<Map<String,Object>> create(@RequestBody Map<String,Object> b) {
        var usuario = (String) b.get("usuario");
        var password = (String) b.get("password");
        var rolId = ((Number) b.get("rolId")).longValue();
        long id = svc.createUser(usuario, password, rolId);
        return ResponseEntity.ok(Map.of("usuario_id", id));
    }

    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> list() {
        return ResponseEntity.ok(svc.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
        @PathVariable long id,
        @RequestBody Map<String,Object> b
    ) {
        var usuario = (String) b.get("usuario");
        var password = (String) b.get("password");
        var rolId = ((Number) b.get("rolId")).longValue();
        svc.updateUser(id, usuario, password, rolId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        svc.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
