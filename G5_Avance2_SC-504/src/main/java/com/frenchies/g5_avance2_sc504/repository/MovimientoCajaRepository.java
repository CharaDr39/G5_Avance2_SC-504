package com.frenchies.g5_avance2_sc504.repository;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class MovimientoCajaRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall insCall, updCall, delCall, listCall;

    public MovimientoCajaRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @PostConstruct
    public void init() {
        // INSERT  (ojo: aquí es P_ID_CIERRE)
        insCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("MOV_INS_SP")
            .declareParameters(
                new SqlParameter("P_ID_CIERRE",  Types.NUMERIC),
                new SqlParameter("P_TIPO",       Types.VARCHAR),   // 'ENTRADA'|'SALIDA'
                new SqlParameter("P_DESCRIPCION",Types.VARCHAR),
                new SqlParameter("P_MONTO",      Types.NUMERIC),
                new SqlOutParameter("P_OUT_ID",  Types.NUMERIC)
            );

        // UPDATE
        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("MOV_UPD_SP")
            .declareParameters(
                new SqlParameter("P_ID",         Types.NUMERIC),   // id del movimiento
                new SqlParameter("P_TIPO",       Types.VARCHAR),
                new SqlParameter("P_DESCRIPCION",Types.VARCHAR),
                new SqlParameter("P_MONTO",      Types.NUMERIC)
            );

        // DELETE
        delCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("MOV_DEL_SP")
            .declareParameters(new SqlParameter("P_ID", Types.NUMERIC));

        // LIST POR CIERRE (este sí usa P_CIERRE_ID)
        listCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_MOVIMIENTOS_CIERRE_SP")
            .declareParameters(
                new SqlParameter("P_CIERRE_ID", Types.NUMERIC),
                new SqlOutParameter("P_CURSOR", Types.REF_CURSOR)
            )
            .returningResultSet("P_CURSOR", (rs, rn) -> {
                Map<String,Object> m = new HashMap<>();
                try { m.put("ID_MOVIMIENTO", rs.getLong("ID_MOVIMIENTO")); } catch (Exception ignore) {}
                try { m.put("ID_CIERRE",     rs.getLong("ID_CIERRE")); }     catch (Exception ignore) {}
                try { m.put("TIPO",          rs.getString("TIPO")); }        catch (Exception ignore) {}
                try { m.put("DESCRIPCION",   rs.getString("DESCRIPCION")); } catch (Exception ignore) {}
                try { m.put("MONTO",         rs.getBigDecimal("MONTO")); }   catch (Exception ignore) {}
                return m;
            });
    }

    public long insert(long cierreId, String tipo, String descripcion, double monto) {
        var out = insCall.execute(Map.of(
            "P_ID_CIERRE",  cierreId,
            "P_TIPO",       tipo,
            "P_DESCRIPCION",(descripcion == null ? "" : descripcion),
            "P_MONTO",      monto
        ));
        return ((Number) out.get("P_OUT_ID")).longValue();
    }

    public void update(long movId, String tipo, String descripcion, double monto) {
        updCall.execute(Map.of(
            "P_ID",          movId,
            "P_TIPO",        tipo,
            "P_DESCRIPCION", (descripcion == null ? "" : descripcion),
            "P_MONTO",       monto
        ));
    }

    public void delete(long movId) {
        delCall.execute(Map.of("P_ID", movId));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> listByCierre(long cierreId) {
        return (List<Map<String,Object>>) listCall
            .execute(Map.of("P_CIERRE_ID", cierreId))
            .get("P_CURSOR");
    }
}
