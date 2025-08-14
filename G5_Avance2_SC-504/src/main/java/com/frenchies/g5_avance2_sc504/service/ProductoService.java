package com.frenchies.g5_avance2_sc504.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.frenchies.g5_avance2_sc504.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    public long crear(String nombre, Number precio) {
        return repo.insert(nombre, precio);
    }

    public void actualizar(long id, String nombre, Number precio) {
        repo.update(id, nombre, precio);
    }

    public void eliminar(long id) {
        repo.delete(id);
    }

    public List<Map<String,Object>> listar() {
        return repo.list();
    }
}
