package com.frenchies.g5_avance2_sc504.service;

import org.springframework.stereotype.Service;
import com.frenchies.g5_avance2_sc504.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepo;

    public RoleService(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    /**
     * Crea un rol llamando a INS_ROL y devuelve el ID generado.
     */
    public long createRole(String nombre, String descripcion) {
        return roleRepo.insertRole(nombre, descripcion);
    }
}
