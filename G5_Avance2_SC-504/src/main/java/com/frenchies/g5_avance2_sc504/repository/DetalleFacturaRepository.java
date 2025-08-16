// src/main/java/com/frenchies/g5_avance2_sc504/repository/DetalleFacturaRepository.java
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
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class DetalleFacturaRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall updCall, delCall, listByFacCall;

    public DetalleFacturaRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @PostConstruct
    public void init() {
        // UPDATE: DET_UPD_SP(P_ID, P_CANTIDAD, P_PRECIO_UNITARIO)
        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("DET_UPD_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_ID", Types.NUMERIC),
                new SqlParameter("P_CANTIDAD", Types.NUMERIC),
                new SqlParameter("P_PRECIO_UNITARIO", Types.NUMERIC) // NUMBER -> NUMERIC
            );

        // DELETE: DET_DEL_SP(P_ID)
        delCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("DET_DEL_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(new SqlParameter("P_ID", Types.NUMERIC));

        // LIST: LST_DETALLE_FACTURA_SP(P_FACTURA_ID IN, P_CURSOR OUT)
        listByFacCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_DETALLE_FACTURA_SP")
            .withoutProcedureColumnMetaDataAccess()
            .declareParameters(
                new SqlParameter("P_FACTURA_ID", Types.NUMERIC),
                new SqlOutParameter("P_CURSOR", Types.REF_CURSOR)
            )
            .returningResultSet("P_CURSOR", (rs, rn) -> Map.of(
                "ID_DETALLE",      rs.getLong("ID_DETALLE"),
                "FACTURA_ID",      rs.getLong("FACTURA_ID"),
                "PRODUCTO_ID",     rs.getLong("PRODUCTO_ID"),
                "CANTIDAD",        rs.getLong("CANTIDAD"),
                "PRECIO_UNITARIO", rs.getDouble("PRECIO_UNITARIO")
            ));
    }

    // INSERT por posición (firma real: P_FACTURA_ID, P_PRODUCTO_ID, P_CANTIDAD, P_PRECIO_UNITARIO, P_OUT_ID)
    public long insertLinea(long facturaId, long productoId, long cantidad, double precioUnitario) {
        String sql = "{ call PKG_FRENCHIES.DET_INS_SP(?, ?, ?, ?, ?) }";
        return jdbc.execute((Connection con) -> {
            CallableStatement cs = con.prepareCall(sql);
            cs.setLong(1, facturaId);
            cs.setLong(2, productoId);
            cs.setLong(3, cantidad);
            cs.setBigDecimal(4, BigDecimal.valueOf(precioUnitario));
            cs.registerOutParameter(5, Types.NUMERIC);
            return cs;
        }, (CallableStatementCallback<Long>) cs -> {
            cs.execute();
            Number n = (Number) cs.getObject(5);
            return n == null ? 0L : n.longValue();
        });
    }

    public void updateLinea(long id, long cantidad, double precioUnitario) {
        updCall.execute(Map.of(
            "P_ID", id,
            "P_CANTIDAD", cantidad,
            "P_PRECIO_UNITARIO", BigDecimal.valueOf(precioUnitario)
        ));
    }

    public void deleteLinea(long id) {
        delCall.execute(Map.of("P_ID", id));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> listByFactura(long facturaId) {
        var out = listByFacCall.execute(Map.of("P_FACTURA_ID", facturaId));
        var key = out.keySet().stream().filter(k -> k.equalsIgnoreCase("P_CURSOR")).findFirst().orElse("P_CURSOR");
        return (List<Map<String,Object>>) out.get(key);
    }
}