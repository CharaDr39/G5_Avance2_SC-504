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
public class ProductoRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall insCall, updCall, delCall, listCall;

    public ProductoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        insCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("PROD_INS_SP")
            .declareParameters(
                new SqlParameter("P_NOMBRE", Types.VARCHAR),
                new SqlParameter("P_PRECIO", Types.NUMERIC),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("PROD_UPD_SP")
            .declareParameters(
                new SqlParameter("P_PRODUCTO_ID", Types.NUMERIC),
                new SqlParameter("P_NOMBRE", Types.VARCHAR),
                new SqlParameter("P_PRECIO", Types.NUMERIC)
            );

        delCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("PROD_DEL_SP")
            .declareParameters(
                new SqlParameter("P_PRODUCTO_ID", Types.NUMERIC)
            );

        // Lista completa: PRODUCTO_ID, NOMBRE, PRECIO
        listCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_PRODUCTOS_COMPLETO_SP")
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR))
            .returningResultSet("P_CURSOR", (rs, rn) -> Map.of(
                "PRODUCTO_ID", rs.getLong("PRODUCTO_ID"),
                "NOMBRE",      rs.getString("NOMBRE"),
                "PRECIO",      rs.getBigDecimal("PRECIO")
            ));
    }

    public long insert(String nombre, Number precio) {
        var out = insCall.execute(Map.of(
            "P_NOMBRE", nombre,
            "P_PRECIO", precio
        ));
        return ((Number) out.get("P_OUT_ID")).longValue();
    }

    public void update(long id, String nombre, Number precio) {
        updCall.execute(Map.of(
            "P_PRODUCTO_ID", id,
            "P_NOMBRE", nombre,
            "P_PRECIO", precio
        ));
    }

    public void delete(long id) {
        delCall.execute(Map.of("P_PRODUCTO_ID", id));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> list() {
        return (List<Map<String,Object>>) listCall.execute(Map.of()).get("P_CURSOR");
    }
}
