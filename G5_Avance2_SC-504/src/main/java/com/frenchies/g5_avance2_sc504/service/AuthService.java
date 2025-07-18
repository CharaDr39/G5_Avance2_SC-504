package com.frenchies.g5_avance2_sc504.service;

import org.springframework.stereotype.Service;
import com.frenchies.g5_avance2_sc504.repository.AuthRepository;

@Service
public class AuthService {
    private final AuthRepository authRepo;

    public AuthService(AuthRepository authRepo) {
        this.authRepo = authRepo;
    }

    /**
     * Llama a PKG_FRENCHIES.F_LOGIN y devuelve el usuario_id (0 si credenciales inválidas).
     */
    public long login(String usuario, String password) {
        return authRepo.login(usuario, password);
    }
}
