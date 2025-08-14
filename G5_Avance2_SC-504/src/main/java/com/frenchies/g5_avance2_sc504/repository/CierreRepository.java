package com.frenchies.g5_avance2_sc504.repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
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
public class CierreRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall insCall, updCall, delCall, listCall, totalFn;

    public CierreRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @PostConstruct
    public void init() {
        // INSERT
        insCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("CIE_INS_SP")
            .declareParameters(
                new SqlParameter("P_TIPO",           Types.VARCHAR),
                new SqlParameter("P_FECHA_CIERRE",   Types.TIMESTAMP),
                new SqlOutParameter("P_OUT_ID",      Types.NUMERIC)
            );

        // UPDATE  (nota: este SP también exige P_TOTAL_FACTURADO)
        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("CIE_UPD_SP")
            .declareParameters(
                new SqlParameter("P_ID",               Types.NUMERIC),
                new SqlParameter("P_TIPO",             Types.VARCHAR),
                new SqlParameter("P_FECHA_CIERRE",     Types.TIMESTAMP),
                new SqlParameter("P_TOTAL_FACTURADO",  Types.NUMERIC)
            );

        // DELETE
        delCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("CIE_DEL_SP")
            .declareParameters(new SqlParameter("P_ID", Types.NUMERIC));

        // LIST
        listCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_CIERRES_SP")
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR))
            .returningResultSet("P_CURSOR", (rs, rn) -> {
                Map<String,Object> m = new HashMap<>();
                try { m.put("ID_CIERRE",       rs.getLong("ID_CIERRE")); }        catch (Exception ignore) {}
                try { m.put("TIPO",            rs.getString("TIPO")); }           catch (Exception ignore) {}
                try { m.put("FECHA_CIERRE",    rs.getTimestamp("FECHA_CIERRE")); }catch (Exception ignore) {}
                try { m.put("TOTAL_FACTURADO", rs.getBigDecimal("TOTAL_FACTURADO")); } catch (Exception ignore) {}
                return m;
            });

        // FUNCIÓN: total por cierre
        totalFn = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withFunctionName("TOTAL_CIERRE_FN")
            .declareParameters(
                new SqlParameter("P_CIERRE_ID", Types.NUMERIC),
                new SqlOutParameter("RETURN",   Types.NUMERIC)
            );
    }

    public long insert(String tipo, LocalDate fecha) {
        Timestamp ts = Timestamp.valueOf(fecha.atStartOfDay());
        var out = insCall.execute(Map.of(
            "P_TIPO",         tipo,
            "P_FECHA_CIERRE", ts
        ));
        return ((Number) out.get("P_OUT_ID")).longValue();
    }

    public void update(long id, String tipo, LocalDate fecha) {
        // calcular total y enviarlo (lo exige el SP)
        double total = total(id);
        Timestamp ts = Timestamp.valueOf(fecha.atStartOfDay());
        updCall.execute(Map.of(
            "P_ID",               id,
            "P_TIPO",             tipo,
            "P_FECHA_CIERRE",     ts,
            "P_TOTAL_FACTURADO",  total
        ));
    }

    public void delete(long id) {
        delCall.execute(Map.of("P_ID", id));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> list() {
        return (List<Map<String,Object>>) listCall.execute(Map.of()).get("P_CURSOR");
    }

    public double total(long cierreId) {
        var out = totalFn.execute(Map.of("P_CIERRE_ID", cierreId));
        return ((Number) out.get("RETURN")).doubleValue();
    }
}
