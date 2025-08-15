// src/main/java/com/frenchies/g5_avance2_sc504/repository/FacturaRepository.java
package com.frenchies.g5_avance2_sc504.repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
        // INSERT (FAC_INS_SP: P_USUARIO_ID IN, P_FACTURA_ID OUT)
        insCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("FAC_INS_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_USUARIO_ID",  Types.NUMERIC),
                new SqlOutParameter("P_FACTURA_ID", Types.NUMERIC)
            );

        // UPDATE
        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("FACT_UPD_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_FACTURA_ID",  Types.NUMERIC),
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
                // Si en tu SELECT devuelves FECHA, puedes mapearla también
                // "FECHA_EMISION", rs.getTimestamp("FECHA_EMISION")
            ));
    }

    // OJO: el segundo parámetro no se usa (se mantiene la firma para no romper servicios)
    public long insertFactura(long usuarioId, double ignorado) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("P_USUARIO_ID", usuarioId);

        Map<String, Object> out = insCall.execute(params);

        Object val = out.get("P_FACTURA_ID");
        if (val == null)
            throw new IllegalStateException("FAC_INS_SP no devolvió P_FACTURA_ID");

        return ((Number) val).longValue();
    }

    public void updateFactura(long id, double monto) {
        updCall.execute(Map.of(
            "P_FACTURA_ID",  id,
            "P_MONTO_TOTAL", monto
        ));
    }

    public void deleteFactura(long id) {
        delCall.execute(Map.of("P_FACTURA_ID", id));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> listFacturas() {
        return (List<Map<String,Object>>) listCall.execute(Map.of()).get("P_CURSOR");
    }
}