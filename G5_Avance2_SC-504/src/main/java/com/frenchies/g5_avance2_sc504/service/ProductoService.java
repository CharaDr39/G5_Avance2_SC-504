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

    private static Number d0(Number n) { return (n == null) ? 0 : n; } // default 0

    public long crear(String nombre, Number precio, Number stockActual, Number puntoReorden) {
        return repo.insert(nombre, d0(precio), d0(stockActual), d0(puntoReorden));
    }

    public void actualizar(long id, String nombre, Number precio, Number stockActual, Number puntoReorden) {
        repo.update(id, nombre, d0(precio), d0(stockActual), d0(puntoReorden));
    }

    public void eliminar(long id) {
        repo.delete(id);
    }

    public List<Map<String,Object>> listar() {
        return repo.list();
    }
}
