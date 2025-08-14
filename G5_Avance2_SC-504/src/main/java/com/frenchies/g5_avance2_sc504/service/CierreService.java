// src/main/java/com/frenchies/g5_avance2_sc504/service/CierreService.java
package com.frenchies.g5_avance2_sc504.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.frenchies.g5_avance2_sc504.repository.CierreRepository;
import com.frenchies.g5_avance2_sc504.repository.MovimientoCajaRepository;

@Service
public class CierreService {

    private final CierreRepository cierres;
    private final MovimientoCajaRepository movs;

    public CierreService(CierreRepository cierres, MovimientoCajaRepository movs) {
        this.cierres = cierres;
        this.movs = movs;
    }

    // Cierres
    public long create(String tipo, LocalDate fecha) { return cierres.insert(tipo, fecha); }
    public void update(long id, String tipo, LocalDate fecha) { cierres.update(id, tipo, fecha); }
    public void delete(long id) { cierres.delete(id); }
    public List<Map<String,Object>> list() { return cierres.list(); }
    public double total(long id) { return cierres.total(id); }

    // Movimientos
    public long addMov(long cierreId, String tipo, String desc, double monto) {
        return movs.insert(cierreId, tipo, desc, monto);
    }
    public void updMov(long movId, String tipo, String desc, double monto) {
        movs.update(movId, tipo, desc, monto);
    }
    public void delMov(long movId) { movs.delete(movId); }
    public List<Map<String,Object>> listMovs(long cierreId) { return movs.listByCierre(cierreId); }
}
