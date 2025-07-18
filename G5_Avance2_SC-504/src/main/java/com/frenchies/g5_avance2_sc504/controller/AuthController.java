package com.frenchies.g5_avance2_sc504.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.frenchies.g5_avance2_sc504.dto.LoginRequest;
import com.frenchies.g5_avance2_sc504.service.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest req) {
        long id = authService.login(req.getUsuario(), req.getPassword());
        if (id > 0) {
            return ResponseEntity.ok(Map.of("usuario_id", id));
        } else {
            return ResponseEntity
                .status(401)
                .body(Map.of("error", "Credenciales inválidas"));
        }
    }
}
