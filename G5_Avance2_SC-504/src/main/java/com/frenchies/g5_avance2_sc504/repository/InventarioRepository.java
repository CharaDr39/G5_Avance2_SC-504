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
public class InventarioRepository {

    private final JdbcTemplate jdbc;

    private SimpleJdbcCall insCall;
    private SimpleJdbcCall updCall;
    private SimpleJdbcCall delCall;
    private SimpleJdbcCall listCall;
    private SimpleJdbcCall productosCall;

    public InventarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        insCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("INV_INS_SP")
            .declareParameters(
                new SqlParameter("P_PRODUCTO_ID", Types.NUMERIC),
                new SqlParameter("P_CANTIDAD_ACTUAL", Types.NUMERIC),
                new SqlParameter("P_STOCK_MINIMO", Types.NUMERIC),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("INV_UPD_SP")
            .declareParameters(
                new SqlParameter("P_ID", Types.NUMERIC),
                new SqlParameter("P_CANTIDAD_ACTUAL", Types.NUMERIC),
                new SqlParameter("P_STOCK_MINIMO", Types.NUMERIC)
            );

        delCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("INV_DEL_SP")
            .declareParameters(
                new SqlParameter("P_ID", Types.NUMERIC)
            );

        // >>> sin RowMapper explícito: mapeo genérico por nombre real de columnas
        listCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_INVENTARIO_SP")
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR));

        productosCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_PRODUCTOS_COMPLETO_SP")
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR));
    }

    public long insertInventario(long productoId, double cantidadActual, double stockMinimo) {
        var out = insCall.execute(Map.of(
            "P_PRODUCTO_ID",     productoId,
            "P_CANTIDAD_ACTUAL", cantidadActual,
            "P_STOCK_MINIMO",    stockMinimo
        ));
        return ((Number) out.get("P_OUT_ID")).longValue();
    }

    public void updateInventario(long id, double cantidadActual, double stockMinimo) {
        updCall.execute(Map.of(
            "P_ID",              id,
            "P_CANTIDAD_ACTUAL", cantidadActual,
            "P_STOCK_MINIMO",    stockMinimo
        ));
    }

    public void deleteInventario(long id) {
        delCall.execute(Map.of("P_ID", id));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> listInventario() {
        return (List<Map<String,Object>>) listCall.execute(Map.of()).get("P_CURSOR");
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> listProductos() {
        return (List<Map<String,Object>>) productosCall.execute(Map.of()).get("P_CURSOR");
    }
}
