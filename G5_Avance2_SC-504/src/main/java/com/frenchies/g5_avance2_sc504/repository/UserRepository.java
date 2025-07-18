package com.frenchies.g5_avance2_sc504.repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall insUserCall;
    private SimpleJdbcCall updUserCall;
    private SimpleJdbcCall delUserCall;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void init() {
        // INS_USUARIO
        this.insUserCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("INS_USUARIO")
            .declareParameters(
                new SqlParameter("P_USUARIO", Types.VARCHAR),
                new SqlParameter("P_PASSWORD", Types.VARCHAR),
                new SqlParameter("P_ROL_ID", Types.NUMERIC),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        // UPD_USUARIO
        this.updUserCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("UPD_USUARIO")
            .declareParameters(
                new SqlParameter("P_USUARIO_ID", Types.NUMERIC),
                new SqlParameter("P_USUARIO", Types.VARCHAR),
                new SqlParameter("P_PASSWORD", Types.VARCHAR),
                new SqlParameter("P_ROL_ID", Types.NUMERIC)
            );

        // DEL_USUARIO
        this.delUserCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("DEL_USUARIO")
            .declareParameters(
                new SqlParameter("P_USUARIO_ID", Types.NUMERIC)
            );
    }

    public long insertUser(String usuario, String password, Long rolId) {
        var out = insUserCall.execute(
            Map.of(
              "P_USUARIO", usuario,
              "P_PASSWORD", password,
              "P_ROL_ID", rolId
            )
        );
        return ((Number) out.get("P_OUT_ID")).longValue();
    }

    public void updateUser(long id, String usuario, String password, Long rolId) {
        var params = new MapSqlParameterSource()
            .addValue("P_USUARIO_ID", id)
            .addValue("P_USUARIO", usuario)
            .addValue("P_PASSWORD", password)
            .addValue("P_ROL_ID", rolId);
        updUserCall.execute(params);
    }

    public void deleteUser(long id) {
        delUserCall.execute(Map.of("P_USUARIO_ID", id));
    }

    public List<Map<String, Object>> listUsers() {
        // Listado puro sin lógica: asignamos alias en mayúsculas
        String sql = 
            "SELECT usuario_id AS \"USUARIO_ID\", " +
            "usuario AS \"USUARIO\", " +
            "rol_id AS \"ROL_ID\" " +
            "FROM usuario " +
            "ORDER BY usuario_id";
        return jdbc.queryForList(sql);
    }
}
