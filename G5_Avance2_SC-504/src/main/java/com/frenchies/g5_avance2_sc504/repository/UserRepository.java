// src/main/java/com/frenchies/g5_avance2_sc504/repository/UserRepository.java
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
public class UserRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall insCall, updCall, delCall, listCall;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        insCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("USU_INS_SP")
            .declareParameters(
                new SqlParameter("P_USUARIO", Types.VARCHAR),
                new SqlParameter("P_PASSWORD", Types.VARCHAR),
                new SqlParameter("P_ROL_ID", Types.NUMERIC),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        updCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("USU_UPD_SP")
            .declareParameters(
                new SqlParameter("P_USUARIO_ID", Types.NUMERIC),
                new SqlParameter("P_USUARIO", Types.VARCHAR),
                new SqlParameter("P_PASSWORD", Types.VARCHAR),
                new SqlParameter("P_ROL_ID", Types.NUMERIC)
            );

        delCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("USU_DEL_SP")
            .declareParameters(
                new SqlParameter("P_USUARIO_ID", Types.NUMERIC)
            );

        listCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("LST_USUARIOS_SP")
            .declareParameters(new SqlOutParameter("P_CURSOR", Types.REF_CURSOR))
            .returningResultSet("P_CURSOR", (rs, rn) -> Map.of(
                "USUARIO_ID", rs.getLong("USUARIO_ID"),
                "USUARIO",    rs.getString("USUARIO"),
                "ROL_ID",     rs.getLong("ROL_ID")
            ));
    }

    public long insertUser(String usuario, String password, long rolId) {
        var out = insCall.execute(
            Map.of("P_USUARIO", usuario, "P_PASSWORD", password, "P_ROL_ID", rolId)
        );
        return ((Number) out.get("P_OUT_ID")).longValue();
    }

    public void updateUser(long id, String usuario, String password, long rolId) {
        updCall.execute(Map.of(
            "P_USUARIO_ID", id,
            "P_USUARIO",    usuario,
            "P_PASSWORD",   password,
            "P_ROL_ID",     rolId
        ));
    }

    public void deleteUser(long id) {
        delCall.execute(Map.of("P_USUARIO_ID", id));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listUsers() {
        return (List<Map<String, Object>>) listCall.execute(Map.of()).get("P_CURSOR");
    }
}
