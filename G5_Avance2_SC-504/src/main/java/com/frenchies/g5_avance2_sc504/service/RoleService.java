package com.frenchies.g5_avance2_sc504.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.frenchies.g5_avance2_sc504.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository repo;
    public RoleService(RoleRepository repo) { this.repo = repo; }

    public long createRole(String nombre, String descripcion) {
        return repo.insertRole(nombre, descripcion);
    }

    public List<Map<String, Object>> getAllRoles() {
        return repo.listRoles();
    }

    public void updateRole(long id, String nombre, String descripcion) {
        repo.updateRole(id, nombre, descripcion);
    }

    public void deleteRole(long id) {
        repo.deleteRole(id);
    }
}
