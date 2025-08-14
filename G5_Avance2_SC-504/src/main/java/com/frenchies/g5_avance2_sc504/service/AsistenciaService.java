package com.frenchies.g5_avance2_sc504.service;

import com.frenchies.g5_avance2_sc504.repository.AsistenciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class AsistenciaService {

    private final AsistenciaRepository repo;

    public AsistenciaService(AsistenciaRepository repo) {
        this.repo = repo;
    }

    public long crear(long usuarioId, String fecha, String hEntrada, String hSalida, String hAlmuerzo) {
        LocalDate d = LocalDate.parse(fecha); // "yyyy-MM-dd"
        LocalTime he = (hEntrada  == null || hEntrada.isBlank())  ? null : LocalTime.parse(hEntrada);  // "HH:mm"
        LocalTime hs = (hSalida   == null || hSalida.isBlank())   ? null : LocalTime.parse(hSalida);
        LocalTime ha = (hAlmuerzo == null || hAlmuerzo.isBlank()) ? null : LocalTime.parse(hAlmuerzo);
        return repo.crear(usuarioId, d, he, hs, ha);
    }

    public void actualizar(long asistenciaId, String fecha, String hSalida, String hAlmuerzo) {
        LocalDate d = (fecha == null || fecha.isBlank()) ? null : LocalDate.parse(fecha);
        LocalTime hs = (hSalida   == null || hSalida.isBlank())   ? null : LocalTime.parse(hSalida);
        LocalTime ha = (hAlmuerzo == null || hAlmuerzo.isBlank()) ? null : LocalTime.parse(hAlmuerzo);
        repo.actualizar(asistenciaId, d, hs, ha);
    }

    public void eliminar(long asistenciaId) {
        repo.eliminar(asistenciaId);
    }

    public List<Map<String, Object>> hoy() {
        return repo.hoy();
    }
}
