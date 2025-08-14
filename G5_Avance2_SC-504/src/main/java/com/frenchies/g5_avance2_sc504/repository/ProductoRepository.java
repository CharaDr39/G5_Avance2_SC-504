package com.frenchies.g5_avance2_sc504.repository;

import java.sql.Types;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
        // INSERT (incluye stock_actual y punto_reorden)
        insCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("PROD_INS_SP")
            .declareParameters(
                new SqlParameter("P_NOMBRE", Types.VARCHAR),
                new SqlParameter("P_PRECIO", Types.NUMERIC),
                new SqlParameter("P_STOCK_ACTUAL", Types.NUMERIC),
                new SqlParameter("P_PUNTO_REORDEN", Types.NUMERIC),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        // UPDATE (incluye stock_actual y punto_reorden)
        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("PROD_UPD_SP")
            .declareParameters(
                new SqlParameter("P_PRODUCTO_ID", Types.NUMERIC),
                new SqlParameter("P_NOMBRE", Types.VARCHAR),
                new SqlParameter("P_PRECIO", Types.NUMERIC),
                new SqlParameter("P_STOCK_ACTUAL", Types.NUMERIC),
                new SqlParameter("P_PUNTO_REORDEN", Types.NUMERIC)
            );

        delCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("PROD_DEL_SP")
            .declareParameters(new SqlParameter("P_PRODUCTO_ID", Types.NUMERIC));

        // LISTA (RowMapper tolerante a columnas/valores nulos)
        listCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_PRODUCTOS_COMPLETO_SP")
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR))
            .returningResultSet("P_CURSOR", (ResultSet rs, int rn) -> {
                Map<String,Object> m = new HashMap<>();
                m.put("PRODUCTO_ID",   getObj(rs, "PRODUCTO_ID"));
                m.put("NOMBRE",        getObj(rs, "NOMBRE"));
                m.put("PRECIO",        getObj(rs, "PRECIO"));
                // Estas pueden no venir en todos los esquemas: deja null si no existen
                m.put("STOCK_ACTUAL",  getObjQuiet(rs, "STOCK_ACTUAL"));
                m.put("PUNTO_REORDEN", getObjQuiet(rs, "PUNTO_REORDEN"));
                return m;
            });
    }

    private static Object getObj(ResultSet rs, String col) throws java.sql.SQLException {
        return rs.getObject(col);
    }
    private static Object getObjQuiet(ResultSet rs, String col) {
        try { return rs.getObject(col); } catch (Exception e) { return null; }
    }

    public long insert(String nombre, Number precio, Number stockActual, Number puntoReorden) {
        var out = insCall.execute(Map.of(
            "P_NOMBRE",        nombre,
            "P_PRECIO",        precio == null ? 0 : precio,
            "P_STOCK_ACTUAL",  stockActual == null ? 0 : stockActual,
            "P_PUNTO_REORDEN", puntoReorden == null ? 0 : puntoReorden
        ));
        return ((Number) out.get("P_OUT_ID")).longValue();
    }

    public void update(long id, String nombre, Number precio, Number stockActual, Number puntoReorden) {
        updCall.execute(Map.of(
            "P_PRODUCTO_ID",   id,
            "P_NOMBRE",        nombre,
            "P_PRECIO",        precio == null ? 0 : precio,
            "P_STOCK_ACTUAL",  stockActual == null ? 0 : stockActual,
            "P_PUNTO_REORDEN", puntoReorden == null ? 0 : puntoReorden
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
