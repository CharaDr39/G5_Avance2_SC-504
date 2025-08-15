// src/main/java/com/frenchies/g5_avance2_sc504/repository/FacturaRepository.java
package com.frenchies.g5_avance2_sc504.repository;

import jakarta.annotation.PostConstruct;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class FacturaRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall insCall, updCall, delCall, listCall;

    public FacturaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        // INSERT
        insCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("FACT_INS_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_USUARIO_ID", Types.NUMERIC),
                new SqlParameter("P_MONTO_TOTAL", Types.DECIMAL),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        // UPDATE
        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("FACT_UPD_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_FACTURA_ID", Types.NUMERIC),
                new SqlParameter("P_MONTO_TOTAL", Types.DECIMAL)
            );

        // DELETE
        delCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("FACT_DEL_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_FACTURA_ID", Types.NUMERIC)
            );

        // LIST
        listCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_FACTURAS_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR))
            .returningResultSet("P_CURSOR", (rs, rn) -> Map.of(
                "FACTURA_ID",  rs.getLong("FACTURA_ID"),
                "USUARIO_ID",  rs.getLong("USUARIO_ID"),
                "MONTO_TOTAL", rs.getDouble("MONTO_TOTAL")
                // Si tu cursor también trae fecha, lee con:
                // "FECHA", rs.getTimestamp("FECHA")
            ));
    }

    public long insertFactura(long usuarioId, double monto) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("P_USUARIO_ID", usuarioId)
            .addValue("P_MONTO_TOTAL", monto);

        Map<String, Object> out = insCall.execute(params);

        Object val = out.get("P_OUT_ID");
        if (val == null) {
            throw new IllegalStateException("FACT_INS_SP no devolvió P_OUT_ID (revisa el paquete/trigger).");
        }
        return ((Number) val).longValue();
    }

    public void updateFactura(long id, double monto) {
        updCall.execute(new MapSqlParameterSource()
            .addValue("P_FACTURA_ID", id)
            .addValue("P_MONTO_TOTAL", monto));
    }

    public void deleteFactura(long id) {
        delCall.execute(new MapSqlParameterSource()
            .addValue("P_FACTURA_ID", id));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listFacturas() {
        return (List<Map<String, Object>>) listCall
            .execute(new MapSqlParameterSource())
            .get("P_CURSOR");
    }
}