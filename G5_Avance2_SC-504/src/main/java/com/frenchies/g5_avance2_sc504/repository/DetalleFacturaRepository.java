package com.frenchies.g5_avance2_sc504.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
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
public class DetalleFacturaRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall listCall, insCall, updCall, delCall;

    public DetalleFacturaRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @PostConstruct
    public void init() {
        // LISTAR
        listCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_DETALLE_FACTURA_SP")
            .declareParameters(
                new SqlParameter("P_FACTURA_ID", Types.NUMERIC),
                new SqlOutParameter("P_CURSOR", Types.REF_CURSOR)
            )
            .returningResultSet("P_CURSOR", (rs, rn) -> Map.of(
                // acepta ID_DETALLE o DETALLE_ID, etc.
                "DETALLE_ID",  getLong(rs, "DETALLE_ID", "ID_DETALLE"),
                "FACTURA_ID",  getLong(rs, "FACTURA_ID", "ID_FACTURA"),
                "PRODUCTO_ID", getLong(rs, "PRODUCTO_ID", "ID_PRODUCTO"),
                "NOMBRE",      getString(rs, "NOMBRE", "PRODUCTO"),
                "CANTIDAD",    getLong(rs, "CANTIDAD"),
                "PRECIO",      getDouble(rs, "PRECIO", "PRECIO_UNITARIO"),
                "SUBTOTAL",    getDouble(rs, "SUBTOTAL")
            ));

        // INSERTAR LÍNEA
        insCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("DET_INS_SP")
            .declareParameters(
                new SqlParameter("P_FACTURA_ID",  Types.NUMERIC),
                new SqlParameter("P_PRODUCTO_ID", Types.NUMERIC),
                new SqlParameter("P_CANTIDAD",    Types.NUMERIC),
                new SqlParameter("P_PRECIO",      Types.NUMERIC),
                new SqlOutParameter("P_OUT_ID",   Types.NUMERIC)
            );

        // ACTUALIZAR LÍNEA
        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("DET_UPD_SP")
            .declareParameters(
                new SqlParameter("P_DETALLE_ID",  Types.NUMERIC),
                new SqlParameter("P_CANTIDAD",    Types.NUMERIC),
                new SqlParameter("P_PRECIO",      Types.NUMERIC)
            );

        // ELIMINAR LÍNEA
        delCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("DET_DEL_SP")
            .declareParameters(
                new SqlParameter("P_DETALLE_ID", Types.NUMERIC)
            );
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> listByFactura(long facturaId) {
        return (List<Map<String,Object>>) listCall.execute(
            Map.of("P_FACTURA_ID", facturaId)
        ).get("P_CURSOR");
    }

    public long insertLinea(long facturaId, long productoId, long cantidad, double precio) {
        var out = insCall.execute(Map.of(
            "P_FACTURA_ID",  facturaId,
            "P_PRODUCTO_ID", productoId,
            "P_CANTIDAD",    cantidad,
            "P_PRECIO",      precio
        ));
        return ((Number) out.get("P_OUT_ID")).longValue();
    }

    public void updateLinea(long detalleId, long cantidad, double precio) {
        updCall.execute(Map.of(
            "P_DETALLE_ID", detalleId,
            "P_CANTIDAD",   cantidad,
            "P_PRECIO",     precio
        ));
    }

    public void deleteLinea(long detalleId) {
        delCall.execute(Map.of("P_DETALLE_ID", detalleId));
    }

    // ===== helpers tolerantes a alias =====
    private static long getLong(ResultSet rs, String... names) throws SQLException {
        for (String n : names) {
            try { long v = rs.getLong(n); if (!rs.wasNull()) return v; return 0L; }
            catch (SQLException ignore) {}
        }
        return 0L;
    }
    private static double getDouble(ResultSet rs, String... names) throws SQLException {
        for (String n : names) {
            try { double v = rs.getDouble(n); if (!rs.wasNull()) return v; return 0.0; }
            catch (SQLException ignore) {}
        }
        return 0.0;
    }
    private static String getString(ResultSet rs, String... names) throws SQLException {
        for (String n : names) {
            try { String v = rs.getString(n); if (v != null) return v; }
            catch (SQLException ignore) {}
        }
        return null;
    }
}
