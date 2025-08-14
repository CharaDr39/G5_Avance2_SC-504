package com.frenchies.g5_avance2_sc504.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.frenchies.g5_avance2_sc504.repository.InventarioRepository;

@Service
public class InventarioService {

    private final InventarioRepository repo;

    public InventarioService(InventarioRepository repo) {
        this.repo = repo;
    }

    public long crear(long productoId, double cantidadActual, double stockMinimo) {
        return repo.insertInventario(productoId, cantidadActual, stockMinimo);
    }

    public void actualizar(long id, double cantidadActual, double stockMinimo) {
        repo.updateInventario(id, cantidadActual, stockMinimo);
    }

    public void eliminar(long id) {
        repo.deleteInventario(id);
    }

    public List<Map<String,Object>> listar() {
        return repo.listInventario();
    }

    public List<Map<String,Object>> productos() {
        return repo.listProductos();
    }
}
