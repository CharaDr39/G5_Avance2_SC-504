// src/main/java/com/frenchies/g5_avance2_sc504/repository/RoleRepository.java
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
public class RoleRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall insCall, updCall, delCall, listCall;

    public RoleRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        insCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("ROL_INS_SP")
            .declareParameters(
                new SqlParameter("P_NOMBRE", Types.VARCHAR),
                new SqlParameter("P_DESCRIPCION", Types.VARCHAR),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("ROL_UPD_SP")
            .declareParameters(
                new SqlParameter("P_ROL_ID", Types.NUMERIC),
                new SqlParameter("P_NOMBRE", Types.VARCHAR),
                new SqlParameter("P_DESCRIPCION", Types.VARCHAR)
            );

        delCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("ROL_DEL_SP")
            .declareParameters(
                new SqlParameter("P_ROL_ID", Types.NUMERIC)
            );

        listCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_ROLES_SP")
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR))
            .returningResultSet("P_CURSOR", (rs, rn) -> Map.of(
                "ROL_ID", rs.getLong("ROL_ID"),
                "NOMBRE", rs.getString("NOMBRE"),
                "DESCRIPCION", rs.getString("DESCRIPCION")
            ));
    }

    public long insertRole(String nombre, String descripcion) {
        var out = insCall.execute(Map.of("P_NOMBRE", nombre, "P_DESCRIPCION", descripcion));
        return ((Number) out.get("P_OUT_ID")).longValue();
    }

    public void updateRole(long id, String nombre, String descripcion) {
        updCall.execute(Map.of("P_ROL_ID", id, "P_NOMBRE", nombre, "P_DESCRIPCION", descripcion));
    }

    public void deleteRole(long id) {
        delCall.execute(Map.of("P_ROL_ID", id));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listRoles() {
        return (List<Map<String, Object>>) listCall.execute(Map.of()).get("P_CURSOR");
    }
}
