package com.frenchies.g5_avance2_sc504.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.frenchies.g5_avance2_sc504.dto.UpdateRoleRequest;
import com.frenchies.g5_avance2_sc504.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepo;

    public RoleService(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    // Create
    public long createRole(String nombre, String descripcion) {
        return roleRepo.insertRole(nombre, descripcion);
    }

    // Read (list)
    public List<Map<String, Object>> listRoles() {
        return roleRepo.listRoles();
    }

    // Update
    public void updateRole(long id, String nombre, String descripcion) {
        roleRepo.updateRole(id, nombre, descripcion);
    }

    // Delete
    public void deleteRole(long id) {
        roleRepo.deleteRole(id);
    }
}
