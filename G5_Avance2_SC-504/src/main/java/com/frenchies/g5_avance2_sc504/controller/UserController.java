package com.frenchies.g5_avance2_sc504.controller;

import java.util.List;
import java.util.Map;

import com.frenchies.g5_avance2_sc504.dto.CreateUserRequest;
import com.frenchies.g5_avance2_sc504.dto.UpdateUserRequest;
import com.frenchies.g5_avance2_sc504.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    private final UserService svc;

    public UserController(UserService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody CreateUserRequest req) {
        long id = svc.createUser(req);
        return ResponseEntity.ok(Map.of("usuario_id", id));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() {
        return ResponseEntity.ok(svc.listUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
        @PathVariable long id,
        @RequestBody UpdateUserRequest req
    ) {
        svc.updateUser(id, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        svc.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
