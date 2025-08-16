// src/main/java/com/frenchies/g5_avance2_sc504/repository/FacturaRepository.java
package com.frenchies.g5_avance2_sc504.repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class FacturaRepository {

    private final JdbcTemplate jdbc;

    // Solo usamos SimpleJdbcCall para el LIST (por cursor)
    private SimpleJdbcCall listCall;

    public FacturaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        // LIST: LST_FACTURAS_SP(P_CURSOR OUT)
        listCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_FACTURAS_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR))
            .returningResultSet("P_CURSOR", (rs, rn) -> Map.of(
                "FACTURA_ID",  rs.getLong("FACTURA_ID"),
                "USUARIO_ID",  rs.getLong("USUARIO_ID"),
                "MONTO_TOTAL", rs.getDouble("MONTO_TOTAL")
            ));
    }

    // FAC_INS_SP(P_USUARIO_ID IN NUMBER, P_FACTURA_ID OUT NUMBER)
    public long insertFactura(long usuarioId, double ignoredMonto) {
        final String sql = "{ call PKG_FRENCHIES.FAC_INS_SP(?, ?) }";
        return jdbc.execute((Connection con) -> {
            CallableStatement cs = con.prepareCall(sql);
            cs.setLong(1, usuarioId);         // P_USUARIO_ID
            cs.registerOutParameter(2, Types.NUMERIC); // P_FACTURA_ID
            return cs;
        }, (CallableStatementCallback<Long>) cs -> {
            cs.execute();
            Number n = (Number) cs.getObject(2);
            if (n == null) throw new IllegalStateException("FAC_INS_SP no devolvió P_FACTURA_ID");
            return n.longValue();
        });
    }

    // FAC_UPD_SP(P_FACTURA_ID IN NUMBER, P_MONTO_TOTAL IN NUMBER)
    public void updateFactura(long id, double monto) {
        final String sql = "{ call PKG_FRENCHIES.FAC_UPD_SP(?, ?) }";
        jdbc.execute((Connection con) -> {
            CallableStatement cs = con.prepareCall(sql);
            cs.setLong(1, id);                                  // P_FACTURA_ID
            cs.setBigDecimal(2, BigDecimal.valueOf(monto));     // P_MONTO_TOTAL
            return cs;
        }, (CallableStatementCallback<Void>) cs -> { cs.execute(); return null; });
    }

    // FAC_DEL_SP(P_ID IN NUMBER)
    public void deleteFactura(long id) {
        final String sql = "{ call PKG_FRENCHIES.FAC_DEL_SP(?) }";
        jdbc.execute((Connection con) -> {
            CallableStatement cs = con.prepareCall(sql);
            cs.setLong(1, id); // P_ID
            return cs;
        }, (CallableStatementCallback<Void>) cs -> { cs.execute(); return null; });
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> listFacturas() {
        return (List<Map<String,Object>>) listCall.execute(Map.of()).get("P_CURSOR");
    }
}