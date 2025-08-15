package com.frenchies.g5_avance2_sc504.repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class DetalleFacturaRepository {

    private final JdbcTemplate jdbc;

    private SimpleJdbcCall detInsCall;
    private SimpleJdbcCall detUpdCall;
    private SimpleJdbcCall detDelCall;
    private SimpleJdbcCall detListCall;
    private SimpleJdbcCall getPrecioFn;

    public DetalleFacturaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        detInsCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("DET_INS_SP")
            .declareParameters(
                new SqlParameter("P_FACTURA_ID", Types.NUMERIC),
                new SqlParameter("P_PRODUCTO_ID", Types.NUMERIC),
                new SqlParameter("P_CANTIDAD", Types.NUMERIC),
                new SqlParameter("P_PRECIO_UNITARIO", Types.NUMERIC),
                // el body puede llamarlo P_OUT_ID o P_ID_DETALLE: soporta ambos
                new SqlOutParameter("P_ID_DETALLE", Types.NUMERIC),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        detUpdCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("DET_UPD_SP")
            .declareParameters(
                new SqlParameter("P_ID", Types.NUMERIC),
                new SqlParameter("P_CANTIDAD", Types.NUMERIC),
                new SqlParameter("P_PRECIO_UNITARIO", Types.NUMERIC)
            );

        detDelCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("DET_DEL_SP")
            .declareParameters(new SqlParameter("P_ID", Types.NUMERIC));

        RowMapper<Map<String,Object>> rm = (rs, rn) -> Map.of(
            "ID_DETALLE",      rs.getLong("ID_DETALLE"),
            "FACTURA_ID",      rs.getLong("FACTURA_ID"),
            "PRODUCTO_ID",     rs.getLong("PRODUCTO_ID"),
            "CANTIDAD",        rs.getInt("CANTIDAD"),
            "PRECIO_UNITARIO", rs.getDouble("PRECIO_UNITARIO")
        );

        detListCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_DETALLE_FACTURA_SP")
            .declareParameters(
                new SqlParameter("P_FACTURA_ID", Types.NUMERIC),
                new SqlOutParameter("P_CURSOR", Types.REF_CURSOR)
            )
            .returningResultSet("P_CURSOR", rm);

        getPrecioFn = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withFunctionName("OBTENER_PRECIO_VENTA_FN")
            .declareParameters(new SqlParameter("P_PRODUCTO_ID", Types.NUMERIC));
    }

    /* ===== canónico ===== */
    public long insertDetalle(long facturaId, long productoId, int cantidad, Double precioUnitario) {
        double precio = (precioUnitario != null) ? precioUnitario : obtenerPrecioVenta(productoId);

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("P_FACTURA_ID", facturaId)
            .addValue("P_PRODUCTO_ID", productoId)
            .addValue("P_CANTIDAD", cantidad)
            .addValue("P_PRECIO_UNITARIO", precio);

        Map<String, Object> out = detInsCall.execute(params);
        Object val = out.get("P_ID_DETALLE");
        if (val == null) val = out.get("P_OUT_ID");
        if (val == null) throw new IllegalStateException("DET_INS_SP no devolvió el id del detalle.");
        return ((Number) val).longValue();
    }

    public void updateDetalle(long idDetalle, int cantidad, double precioUnitario) {
        detUpdCall.execute(new MapSqlParameterSource()
            .addValue("P_ID", idDetalle)
            .addValue("P_CANTIDAD", cantidad)
            .addValue("P_PRECIO_UNITARIO", precioUnitario));
    }

    public void deleteDetalle(long idDetalle) {
        detDelCall.execute(new MapSqlParameterSource().addValue("P_ID", idDetalle));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> listByFactura(long facturaId) {
        return (List<Map<String,Object>>) detListCall
            .execute(new MapSqlParameterSource().addValue("P_FACTURA_ID", facturaId))
            .get("P_CURSOR");
    }

    private double obtenerPrecioVenta(long productoId) {
        Number n = getPrecioFn.executeFunction(Number.class,
            new MapSqlParameterSource().addValue("P_PRODUCTO_ID", productoId));
        return (n != null) ? n.doubleValue() : 0.0;
    }

    /* ===== alias para mantener compatibilidad con tu servicio ===== */
    public long insertLinea(long facturaId, long productoId, int cantidad, Double precioUnitario) {
        return insertDetalle(facturaId, productoId, cantidad, precioUnitario);
    }
    public long insertLinea(Long facturaId, Long productoId, Integer cantidad, Double precioUnitario) {
        return insertDetalle(facturaId, productoId, cantidad, precioUnitario);
    }
    public long insertLinea(long facturaId, long productoId, int cantidad, double precioUnitario) {
        return insertDetalle(facturaId, productoId, cantidad, precioUnitario);
    }
}