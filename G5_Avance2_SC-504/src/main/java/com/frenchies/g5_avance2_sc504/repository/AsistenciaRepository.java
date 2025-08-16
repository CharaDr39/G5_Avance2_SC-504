// src/main/java/com/frenchies/g5_avance2_sc504/repository/AsistenciaRepository.java
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Repository
public class AsistenciaRepository {

    private final JdbcTemplate jdbc;

    // INSERT y listados
    private SimpleJdbcCall asiIns;
    private SimpleJdbcCall lstHoy;
    private SimpleJdbcCall lstAll;

    // UPDATE: dos variantes (ASI_* y ASIS_*)
    private SimpleJdbcCall asiUpd_Pid;            // ASI_UPD_SP(P_ID, P_FECHA, P_HORA_SALIDA, P_HORA_ALMUERZO)
    private SimpleJdbcCall asisUpd_Pasistencia;   // ASIS_UPD_SP(P_ASISTENCIA_ID, P_HORA_SALIDA, P_HORA_ALMUERZO)

    // DELETE: dos variantes (ASI_* y ASIS_*)
    private SimpleJdbcCall asiDel_Pid;            // ASI_DEL_SP(P_ID)
    private SimpleJdbcCall asisDel_Pasistencia;   // ASIS_DEL_SP(P_ASISTENCIA_ID)

    private static final DateTimeFormatter DF_DATE = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter DF_TIME = DateTimeFormatter.ofPattern("HH:mm");

    public AsistenciaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void init() {
        // ===== INSERT: ASI_INS_SP =====
        asiIns = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("ASI_INS_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_USUARIO_ID", Types.NUMERIC),
                new SqlParameter("P_FECHA", Types.TIMESTAMP),
                new SqlParameter("P_HORA_ENTRADA", Types.TIMESTAMP),
                new SqlParameter("P_HORA_SALIDA", Types.TIMESTAMP),
                new SqlParameter("P_HORA_ALMUERZO", Types.TIMESTAMP),
                new SqlOutParameter("P_ID", Types.NUMERIC)
            );

        // ===== LIST HOY: LST_ASISTENCIA_HOY_SP =====
        lstHoy = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_ASISTENCIA_HOY_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR))
            .returningResultSet("P_CURSOR", (rs, rn) -> {
                // Partimos del mapeo automático…
                Map<String,Object> row = new ColumnMapRowMapper().mapRow(rs, rn);
                // …y normalizamos campos de fecha/hora si existen
                normalizeRowDateTime(row);
                return row;
            });

        // ===== LIST ALL: LST_ASISTENCIA_SP =====
        lstAll = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_ASISTENCIA_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR))
            .returningResultSet("P_CURSOR", (rs, rn) -> {
                Map<String,Object> row = new ColumnMapRowMapper().mapRow(rs, rn);
                normalizeRowDateTime(row);
                return row;
            });

        // ===== UPDATE (variantes) =====
        // Firma real: P_ID, P_FECHA, P_HORA_SALIDA, P_HORA_ALMUERZO
        asiUpd_Pid = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("ASI_UPD_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_ID", Types.NUMERIC),
                new SqlParameter("P_FECHA", Types.TIMESTAMP),
                new SqlParameter("P_HORA_SALIDA", Types.TIMESTAMP),
                new SqlParameter("P_HORA_ALMUERZO", Types.TIMESTAMP)
            );

        asisUpd_Pasistencia = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("ASIS_UPD_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_ASISTENCIA_ID", Types.NUMERIC),
                new SqlParameter("P_HORA_SALIDA", Types.TIMESTAMP),
                new SqlParameter("P_HORA_ALMUERZO", Types.TIMESTAMP)
            );

        // ===== DELETE (variantes) =====
        asiDel_Pid = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("ASI_DEL_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(new SqlParameter("P_ID", Types.NUMERIC));

        asisDel_Pasistencia = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("ASIS_DEL_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(new SqlParameter("P_ASISTENCIA_ID", Types.NUMERIC));
    }

    private static Timestamp ts(LocalDate d, LocalTime t) {
        if (d == null || t == null) return null;
        return Timestamp.valueOf(d.atTime(t));
    }

    private static void normalizeRowDateTime(Map<String,Object> row) {
        // FECHA (si viene como Timestamp o Date)
        Object f = row.get("FECHA");
        if (f instanceof java.util.Date dt) {
            LocalDate d = new Timestamp(dt.getTime()).toLocalDateTime().toLocalDate();
            row.put("FECHA", DF_DATE.format(d));
        }
        // HORAS
        row.computeIfPresent("HORA_ENTRADA", (k,v) -> toHHmm(v));
        row.computeIfPresent("HORA_SALIDA",  (k,v) -> toHHmm(v));
        row.computeIfPresent("HORA_ALMUERZO",(k,v) -> toHHmm(v));
    }

    private static String toHHmm(Object v) {
        if (v == null) return null;
        if (v instanceof java.util.Date dt) {
            LocalTime t = new Timestamp(dt.getTime()).toLocalDateTime().toLocalTime();
            return DF_TIME.format(t);
        }
        return String.valueOf(v);
    }

    // ===== Crear =====
    public long crear(long usuarioId, LocalDate fecha, LocalTime hEntrada, LocalTime hSalida, LocalTime hAlmuerzo) {
        Map<String, Object> out = asiIns.execute(Map.of(
            "P_USUARIO_ID", usuarioId,
            "P_FECHA", ts(fecha, LocalTime.MIDNIGHT),
            "P_HORA_ENTRADA", ts(fecha, hEntrada),
            "P_HORA_SALIDA", ts(fecha, hSalida),
            "P_HORA_ALMUERZO", ts(fecha, hAlmuerzo)
        ));
        Object val = out.get("P_ID");
        return val == null ? 0L : ((Number) val).longValue();
    }

    // ===== Actualizar =====
    public void actualizar(long asistenciaId, LocalDate fecha, LocalTime hSalida, LocalTime hAlmuerzo) {
        LocalDate base = (fecha != null ? fecha : LocalDate.now());
        Timestamp tsFecha  = Timestamp.valueOf(base.atStartOfDay());
        Timestamp tsSalida = (hSalida == null ? null : Timestamp.valueOf(base.atTime(hSalida)));
        Timestamp tsAlm    = (hAlmuerzo == null ? null : Timestamp.valueOf(base.atTime(hAlmuerzo)));

        // 1) intento con ASI_UPD_SP(P_ID, P_FECHA, P_HORA_SALIDA, P_HORA_ALMUERZO)
        try {
            asiUpd_Pid.execute(Map.of(
                "P_ID", asistenciaId,
                "P_FECHA", tsFecha,
                "P_HORA_SALIDA", tsSalida,
                "P_HORA_ALMUERZO", tsAlm
            ));
            return;
        } catch (Exception ignore) { /* fallback */ }

        // 2) fallback a ASIS_UPD_SP(P_ASISTENCIA_ID, P_HORA_SALIDA, P_HORA_ALMUERZO)
        asisUpd_Pasistencia.execute(Map.of(
            "P_ASISTENCIA_ID", asistenciaId,
            "P_HORA_SALIDA", tsSalida,
            "P_HORA_ALMUERZO", tsAlm
        ));
    }

    // ===== Eliminar =====
    public void eliminar(long asistenciaId) {
        try {
            asiDel_Pid.execute(Map.of("P_ID", asistenciaId));
            return;
        } catch (Exception ignore) { /* fallback */ }
        asisDel_Pasistencia.execute(Map.of("P_ASISTENCIA_ID", asistenciaId));
    }

    // ===== Listados =====
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> hoy() {
        return (List<Map<String, Object>>) lstHoy.execute(Map.of()).get("P_CURSOR");
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listAll() {
        return (List<Map<String, Object>>) lstAll.execute(Map.of()).get("P_CURSOR");
    }
}