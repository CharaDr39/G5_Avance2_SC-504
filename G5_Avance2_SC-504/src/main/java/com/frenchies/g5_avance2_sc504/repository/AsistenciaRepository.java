package com.frenchies.g5_avance2_sc504.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Repository
public class AsistenciaRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall asisIns, asisUpd, asisDel, rptHoy;

    public AsistenciaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void init() {
        asisIns = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("ASIS_INS_SP")
            .declareParameters(
                new SqlParameter("P_USUARIO_ID", Types.NUMERIC),
                new SqlParameter("P_FECHA", Types.TIMESTAMP),
                new SqlParameter("P_HORA_ENTRADA", Types.TIMESTAMP),
                new SqlParameter("P_HORA_SALIDA", Types.TIMESTAMP),
                new SqlParameter("P_HORA_ALMUERZO", Types.TIMESTAMP),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        asisUpd = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("ASIS_UPD_SP")
            .declareParameters(
                new SqlParameter("P_ASISTENCIA_ID", Types.NUMERIC),
                new SqlParameter("P_HORA_SALIDA", Types.TIMESTAMP),
                new SqlParameter("P_HORA_ALMUERZO", Types.TIMESTAMP)
            );

        asisDel = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("ASIS_DEL_SP")
            .declareParameters(new SqlParameter("P_ASISTENCIA_ID", Types.NUMERIC));

        rptHoy = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("RPT_ASISTENCIA_HOY_SP")
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR))
            .returningResultSet("P_CURSOR", new ColumnMapRowMapper());
    }

    private static Timestamp ts(LocalDate d, LocalTime t) {
        if (d == null || t == null) return null;
        return Timestamp.valueOf(d.atTime(t));
    }

    public long crear(long usuarioId, LocalDate fecha, LocalTime hEntrada, LocalTime hSalida, LocalTime hAlmuerzo) {
        Map<String, Object> out = asisIns.execute(Map.of(
            "P_USUARIO_ID", usuarioId,
            "P_FECHA", ts(fecha, LocalTime.MIDNIGHT),
            "P_HORA_ENTRADA", ts(fecha, hEntrada),
            "P_HORA_SALIDA", ts(fecha, hSalida),
            "P_HORA_ALMUERZO", ts(fecha, hAlmuerzo)
        ));
        return ((Number) out.get("P_OUT_ID")).longValue();
    }

    public void actualizar(long asistenciaId, LocalDate fecha, LocalTime hSalida, LocalTime hAlmuerzo) {
        // si viene fecha, la usamos solo para construir las horas; si no, no importa (Oracle ignora la parte de fecha en TIME)
        Timestamp tsSalida = (hSalida == null ? null : Timestamp.valueOf((fecha != null ? fecha : LocalDate.now()).atTime(hSalida)));
        Timestamp tsAlm   = (hAlmuerzo == null ? null : Timestamp.valueOf((fecha != null ? fecha : LocalDate.now()).atTime(hAlmuerzo)));
        asisUpd.execute(Map.of(
            "P_ASISTENCIA_ID", asistenciaId,
            "P_HORA_SALIDA", tsSalida,
            "P_HORA_ALMUERZO", tsAlm
        ));
    }

    public void eliminar(long asistenciaId) {
        asisDel.execute(Map.of("P_ASISTENCIA_ID", asistenciaId));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> hoy() {
        return (List<Map<String, Object>>) rptHoy.execute(Map.of()).get("P_CURSOR");
    }
}
