package com.frenchies.g5_avance2_sc504.repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class MovimientoCajaRepository {

    private final JdbcTemplate jdbc;

    private SimpleJdbcCall insCall;
    private SimpleJdbcCall updCall;
    private SimpleJdbcCall delCall;
    private SimpleJdbcCall listByCierreCall;

    public MovimientoCajaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        // MOV_INS_SP(p_id_cierre IN, p_tipo IN, p_descripcion IN, p_monto IN, p_out_id OUT)
        insCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("MOV_INS_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_ID_CIERRE", Types.NUMERIC),
                new SqlParameter("P_TIPO", Types.VARCHAR),
                new SqlParameter("P_DESCRIPCION", Types.VARCHAR),
                new SqlParameter("P_MONTO", Types.NUMERIC),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        // MOV_UPD_SP(p_id IN, p_tipo IN, p_descripcion IN, p_monto IN)
        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("MOV_UPD_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_ID", Types.NUMERIC),
                new SqlParameter("P_TIPO", Types.VARCHAR),
                new SqlParameter("P_DESCRIPCION", Types.VARCHAR),
                new SqlParameter("P_MONTO", Types.NUMERIC)
            );

        // MOV_DEL_SP(p_id IN)
        delCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("MOV_DEL_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_ID", Types.NUMERIC)
            );

        // MOV_LST_SP(p_cierre_id IN, p_cursor OUT)
        listByCierreCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("MOV_LST_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_CIERRE_ID", Types.NUMERIC),
                new SqlOutParameter("P_CURSOR", Types.REF_CURSOR)
            )
            .returningResultSet("P_CURSOR", (rs, rn) -> Map.of(
                "ID_MOVIMIENTO", rs.getLong("ID_MOVIMIENTO"),
                "ID_CIERRE",     rs.getLong("ID_CIERRE"),
                "TIPO",          rs.getString("TIPO"),
                "DESCRIPCION",   rs.getString("DESCRIPCION"),
                "MONTO",         rs.getDouble("MONTO")
            ));
    }

    public long insert(long cierreId, String tipo, String descripcion, double monto) {
        var out = insCall.execute(Map.of(
            "P_ID_CIERRE", cierreId,
            "P_TIPO", tipo,
            "P_DESCRIPCION", descripcion,
            "P_MONTO", monto
        ));
        Object val = out.get("P_OUT_ID");
        if (val == null) throw new IllegalStateException("MOV_INS_SP no devolvió P_OUT_ID");
        return ((Number) val).longValue();
    }

    public void update(long movId, String tipo, String descripcion, double monto) {
        updCall.execute(Map.of(
            "P_ID", movId,
            "P_TIPO", tipo,
            "P_DESCRIPCION", descripcion,
            "P_MONTO", monto
        ));
    }

    public void delete(long movId) {
        delCall.execute(Map.of("P_ID", movId));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> listByCierre(long cierreId) {
        var out = listByCierreCall.execute(Map.of("P_CIERRE_ID", cierreId));
        var key = out.keySet().stream()
            .filter(k -> k.equalsIgnoreCase("P_CURSOR"))
            .findFirst().orElse("P_CURSOR");
        return (List<Map<String,Object>>) out.get(key);
    }
}