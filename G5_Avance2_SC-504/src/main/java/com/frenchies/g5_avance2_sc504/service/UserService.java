package com.frenchies.g5_avance2_sc504.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.frenchies.g5_avance2_sc504.dto.CreateUserRequest;
import com.frenchies.g5_avance2_sc504.dto.UpdateUserRequest;
import com.frenchies.g5_avance2_sc504.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public long createUser(CreateUserRequest req) {
        return repo.insertUser(req.getUsuario(), req.getPassword(), req.getRolId());
    }

    public List<Map<String, Object>> listUsers() {
        return repo.listUsers();
    }

    public void updateUser(long id, UpdateUserRequest req) {
        repo.updateUser(id, req.getUsuario(), req.getPassword(), req.getRolId());
    }

    public void deleteUser(long id) {
        repo.deleteUser(id);
    }
}
