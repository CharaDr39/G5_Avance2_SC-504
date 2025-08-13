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
        insCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("FACT_INS_SP")
            .declareParameters(
                new SqlParameter("P_USUARIO_ID", Types.NUMERIC),
                new SqlParameter("P_MONTO_TOTAL", Types.DECIMAL),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("FACT_UPD_SP")
            .declareParameters(
                new SqlParameter("P_FACTURA_ID", Types.NUMERIC),
                new SqlParameter("P_MONTO_TOTAL", Types.DECIMAL)
            );

        delCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("FACT_DEL_SP")
            .declareParameters(
                new SqlParameter("P_FACTURA_ID", Types.NUMERIC)
            );

        listCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_FACTURAS_SP")
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR))
            .returningResultSet("P_CURSOR", (rs, rn) -> Map.of(
                "FACTURA_ID",  rs.getLong("FACTURA_ID"),
                "USUARIO_ID",  rs.getLong("USUARIO_ID"),
                "MONTO_TOTAL", rs.getDouble("MONTO_TOTAL")
            ));
    }

    public long insertFactura(long usuarioId, double monto) {
        var out = insCall.execute(
            Map.of("P_USUARIO_ID", usuarioId, "P_MONTO_TOTAL", monto)
        );
        return ((Number) out.get("P_OUT_ID")).longValue();
    }

    public void updateFactura(long id, double monto) {
        updCall.execute(Map.of("P_FACTURA_ID", id, "P_MONTO_TOTAL", monto));
    }

    public void deleteFactura(long id) {
        delCall.execute(Map.of("P_FACTURA_ID", id));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> listFacturas() {
        return (List<Map<String,Object>>) listCall.execute(Map.of()).get("P_CURSOR");
    }
}
