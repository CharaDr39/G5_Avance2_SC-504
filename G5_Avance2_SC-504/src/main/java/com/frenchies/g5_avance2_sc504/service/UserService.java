
package com.frenchies.g5_avance2_sc504.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.frenchies.g5_avance2_sc504.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repo;
    public UserService(UserRepository repo) { this.repo = repo; }

    public long createUser(String usuario, String password, long rolId) {
        return repo.insertUser(usuario, password, rolId);
    }

    public List<Map<String,Object>> getAllUsers() {
        return repo.listUsers();
    }

    public void updateUser(long id, String usuario, String password, long rolId) {
        repo.updateUser(id, usuario, password, rolId);
    }

    public void deleteUser(long id) {
        repo.deleteUser(id);
    }
}
