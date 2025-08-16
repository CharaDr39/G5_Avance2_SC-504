// src/main/java/com/frenchies/g5_avance2_sc504/repository/CierreRepository.java
package com.frenchies.g5_avance2_sc504.repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class CierreRepository {

    private final JdbcTemplate jdbc;

    private SimpleJdbcCall insCierreCall;            // CIE_INS_SP (sin fecha)
    private SimpleJdbcCall insCierreWithFechaCall;   // CIE_INS_SP (con P_FECHA_CIERRE)
    private SimpleJdbcCall updCierreFechaCall;       // CIE_UPD_SP (P_ID, P_TIPO, P_FECHA_CIERRE)
    private SimpleJdbcCall setTotalCall;             // CIE_TOTAL_SP (P_CIERRE_ID, P_TOTAL)
    private SimpleJdbcCall delCierreCall;            // CIE_DEL_SP
    private SimpleJdbcCall listCierreCall;           // LST_CIERRES_SP
    private SimpleJdbcCall totalFnCall;              // TOTAL_CIERRE_FN (FUNCTION)

    public CierreRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        // === INSERT CIERRE ===
        insCierreCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("CIE_INS_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_TIPO", Types.VARCHAR),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        insCierreWithFechaCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("CIE_INS_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_TIPO", Types.VARCHAR),
                new SqlParameter("P_FECHA_CIERRE", Types.TIMESTAMP),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        // === UPDATE CIERRE ===
        updCierreFechaCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("CIE_UPD_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_ID", Types.NUMERIC),
                new SqlParameter("P_TIPO", Types.VARCHAR),
                new SqlParameter("P_FECHA_CIERRE", Types.TIMESTAMP)
            );

        // === SET TOTAL MANUAL ===
        setTotalCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("CIE_TOTAL_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_CIERRE_ID", Types.NUMERIC),
                new SqlParameter("P_TOTAL", Types.NUMERIC)
            );

        // === DELETE CIERRE ===
        delCierreCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("CIE_DEL_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(new SqlParameter("P_ID", Types.NUMERIC));

        // === LISTAR CIERRES ===
        listCierreCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_CIERRES_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR))
            .returningResultSet("P_CURSOR", (rs, rn) -> Map.of(
                "ID_CIERRE",       rs.getLong("ID_CIERRE"),
                "FECHA_CIERRE",    rs.getTimestamp("FECHA_CIERRE"),
                "TIPO",            rs.getString("TIPO"),
                "TOTAL_FACTURADO", rs.getDouble("TOTAL_FACTURADO")
            ));

        // === TOTAL DE CIERRE (FUNCTION) ===
        totalFnCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withFunctionName("TOTAL_CIERRE_FN")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(new SqlParameter("P_CIERRE_ID", Types.NUMERIC));
    }

    // ===== Cierres =====
    public long insert(String tipo) {
        var out = insCierreCall.execute(Map.of("P_TIPO", tipo));
        Object val = out.get("P_OUT_ID");
        if (val == null) throw new IllegalStateException("CIE_INS_SP no devolvió P_OUT_ID");
        return ((Number) val).longValue();
    }

    public long insert(String tipo, LocalDate fecha) {
        var out = insCierreWithFechaCall.execute(Map.of(
            "P_TIPO", tipo,
            "P_FECHA_CIERRE", Timestamp.valueOf(fecha.atStartOfDay())
        ));
        Object val = out.get("P_OUT_ID");
        if (val == null) throw new IllegalStateException("CIE_INS_SP no devolvió P_OUT_ID");
        return ((Number) val).longValue();
    }

    public void update(long id, String tipo, LocalDate fecha) {
        var ts = (fecha == null)
            ? Timestamp.valueOf(LocalDate.now().atStartOfDay())
            : Timestamp.valueOf(fecha.atStartOfDay());
        updCierreFechaCall.execute(Map.of(
            "P_ID", id,
            "P_TIPO", tipo,
            "P_FECHA_CIERRE", ts
        ));
    }

    // Compatibilidad antigua.
    public void update(long id, String tipo, Double ignoredTotalFacturado) {
        update(id, tipo, LocalDate.now());
    }

    // Setear total manual usando CIE_TOTAL_SP
    public void setTotal(long idCierre, double total) {
        setTotalCall.execute(Map.of(
            "P_CIERRE_ID", idCierre,
            "P_TOTAL", total
        ));
    }

    public void delete(long id) {
        delCierreCall.execute(Map.of("P_ID", id));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> list() {
        return (List<Map<String,Object>>) listCierreCall.execute(Map.of()).get("P_CURSOR");
    }

    public double total(long idCierre) {
        Number n = totalFnCall.executeFunction(Number.class, Map.of("P_CIERRE_ID", idCierre));
        return n == null ? 0.0 : n.doubleValue();
    }
}